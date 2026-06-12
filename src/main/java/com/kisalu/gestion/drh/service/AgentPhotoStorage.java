package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.config.UploadProperties;
import com.kisalu.gestion.drh.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** Stockage et validation des photos agent (equivalent AgentView::post). */
@Component
public class AgentPhotoStorage {

    private static final long MAX_SIZE = 1_048_576L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_MIME = Set.of("image/jpeg", "image/png", "image/webp");

    private final UploadProperties uploadProperties;

    public AgentPhotoStorage(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    public String storeAgentPhoto(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new BusinessException(400, "Le champ de téléchargement de la photo est requis.");
        }
        if (photo.getSize() > MAX_SIZE) {
            throw new BusinessException(400, "L'image ne doit pas dépasser 1 Mo.");
        }

        String originalName = photo.getOriginalFilename();
        String extension = extensionOf(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "Extension non autorisée. Autorisées : jpg, jpeg, png, webp.");
        }

        String mimeType = photo.getContentType();
        if (mimeType == null || !ALLOWED_MIME.contains(mimeType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException(400, "Type MIME non valide. Attendu : JPEG, PNG, ou WEBP.");
        }

        try {
            Path directory = Paths.get(uploadProperties.getPhotosAgentsDir()).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            String filename = "agent_" + UUID.randomUUID() + "." + extension;
            Path target = directory.resolve(filename);
            photo.transferTo(target);
            return uploadProperties.getPublicPhotosPath() + "/" + filename;
        } catch (IOException ex) {
            throw new BusinessException(500, "Impossible de créer le répertoire de stockage des photos.");
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
