package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.config.UploadProperties;
import com.kisalu.gestion.drh.common.exception.BusinessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Specimens (module agent PHP : SpecimenView). */
@Service
public class AgentSpecimenService {

    private static final long PHOTO_MAX = 2 * 1024 * 1024L;
    private static final Set<String> PHOTO_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");
    private static final Set<String> PHOTO_MIME = Set.of("image/jpeg", "image/png", "image/gif");

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UploadProperties uploadProperties;

    public AgentSpecimenService(NamedParameterJdbcTemplate jdbcTemplate, UploadProperties uploadProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.uploadProperties = uploadProperties;
    }

    public Map<String, Object> listSpecimens(Integer id) {
        if (id != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM rf_specimen WHERE id = :id", Map.of("id", id));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Specimen introuvable.");
            }
            return Map.of("code", 200, "message", "Specimen récupéré avec succès.", "data", rows.getFirst());
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_specimen", Map.of());
        return Map.of("code", 200, "message", "Liste des specimens récupérée.", "data", rows);
    }

    @Transactional
    public Map<String, Object> createSpecimen(String libele, String idUserCreated, MultipartFile photo) {
        if (libele == null || libele.isBlank() || idUserCreated == null || idUserCreated.isBlank()) {
            throw new BusinessException(400, "Veuillez fournir 'libele' et 'id_user_created'.");
        }
        if (photo == null || photo.isEmpty()) {
            throw new BusinessException(400, "Aucune image 'photo' envoyée.");
        }

        String photoPath = storeSpecimenPhoto(photo);
        jdbcTemplate.update("""
                INSERT INTO rf_specimen (libele, photo, status, id_user_created, created_at, updated_at)
                VALUES (:libele, :photo, 'active', :userCreated, NOW(), NOW())
                """, Map.of("libele", libele.trim(), "photo", photoPath, "userCreated", idUserCreated.trim()));

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        Map<String, Object> data = jdbcTemplate.queryForList(
                "SELECT * FROM rf_specimen WHERE id = :id", Map.of("id", id)).getFirst();
        return Map.of("code", 200, "message", "Specimen créé avec succès.", "data", data);
    }

    private String storeSpecimenPhoto(MultipartFile photo) {
        if (photo.getSize() > PHOTO_MAX) {
            throw new BusinessException(400, "Type d'image non autorisé.");
        }

        String extension = extensionOf(photo.getOriginalFilename());
        if (!PHOTO_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "Type d'image non autorisé.");
        }

        String mimeType = photo.getContentType();
        if (mimeType == null || !PHOTO_MIME.contains(mimeType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException(400, "Type d'image non autorisé.");
        }

        try {
            Path directory = Paths.get(uploadProperties.getSpecimenDir()).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            String filename = "specimen_" + UUID.randomUUID() + "." + extension;
            photo.transferTo(directory.resolve(filename));
            return "/" + uploadProperties.getPublicSpecimenPath() + "/" + filename;
        } catch (IOException ex) {
            throw new BusinessException(500, "Erreur lors de l'enregistrement du fichier.");
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
