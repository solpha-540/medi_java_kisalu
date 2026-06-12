package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** Stockage generique de documents uploades (PDF/images). */
@Component
public class DocumentStorage {

    public String storeOptional(
            MultipartFile file,
            String fieldName,
            String storageDir,
            String publicPathPrefix,
            long maxSize,
            Set<String> allowedExtensions,
            Set<String> allowedMimeTypes) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return store(file, fieldName, storageDir, publicPathPrefix, "", maxSize, allowedExtensions, allowedMimeTypes);
    }

    public String store(
            MultipartFile file,
            String fieldName,
            String storageDir,
            String publicPathPrefix,
            String filenamePrefix,
            long maxSize,
            Set<String> allowedExtensions,
            Set<String> allowedMimeTypes) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "Le fichier " + fieldName + " est requis.");
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(400, "Le fichier ne doit pas dépasser " + (maxSize / 1024 / 1024) + " Mo.");
        }

        String extension = extensionOf(file.getOriginalFilename());
        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException(400, "Extension non autorisée pour " + fieldName + ".");
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !allowedMimeTypes.contains(mimeType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException(400, "Type MIME non valide pour " + fieldName + ".");
        }

        try {
            Path directory = Paths.get(storageDir).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            String filename = filenamePrefix + UUID.randomUUID() + "." + extension;
            file.transferTo(directory.resolve(filename));
            return publicPathPrefix + "/" + filename;
        } catch (IOException ex) {
            throw new BusinessException(500, "Impossible de stocker le fichier " + fieldName + ".");
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
