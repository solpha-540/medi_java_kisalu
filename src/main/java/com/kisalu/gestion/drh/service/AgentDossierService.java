package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.config.UploadProperties;
import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Dossiers RH agents (DossierViews, UpdateDossierView PHP). */
@Service
public class AgentDossierService {

    private static final List<String> FILE_FIELDS = List.of(
            "curiculum_vitae", "diplome_etat", "diplome_gradua", "diplome_licence");
    private static final Set<String> DOC_EXT = Set.of("pdf", "png", "jpg", "jpeg");
    private static final Set<String> DOC_MIME = Set.of("application/pdf", "image/png", "image/jpeg");
    private static final long DOC_MAX = 5L * 1024 * 1024;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UploadProperties uploadProperties;
    private final AutorisationService autorisationService;

    public AgentDossierService(
            NamedParameterJdbcTemplate jdbcTemplate,
            UploadProperties uploadProperties,
            AutorisationService autorisationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.uploadProperties = uploadProperties;
        this.autorisationService = autorisationService;
    }

    public Map<String, Object> listDossiers() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_dossier", Map.of());
        if (rows.isEmpty()) {
            throw new BusinessException(400, "Aucun dossier trouvé");
        }
        return Map.of("code", 200, "message", "Voici la liste des dossiers", "data", rows);
    }

    @Transactional
    public Map<String, Object> createDossier(String idOpAgent, AuthenticatedUser user, Map<String, MultipartFile> files) {
        if (idOpAgent == null || idOpAgent.isBlank()) {
            throw new BusinessException(400, "Le champ id_op_agent est obligatoire.");
        }

        Map<String, String> uploaded = uploadFiles(files);
        jdbcTemplate.update("""
                INSERT INTO rf_dossier (id_op_agent, id_user_created, curiculum_vitae, diplome_etat,
                    diplome_gradua, diplome_licence, created_at, last_update)
                VALUES (:agentId, :userCreated, :cv, :etat, :gradua, :licence, NOW(), NOW())
                """, Map.of(
                "agentId", Integer.parseInt(idOpAgent),
                "userCreated", user.getUserRf(),
                "cv", uploaded.get("curiculum_vitae"),
                "etat", uploaded.get("diplome_etat"),
                "gradua", uploaded.get("diplome_gradua"),
                "licence", uploaded.get("diplome_licence")));

        return Map.of("code", 200, "message", "Dossier de l'agent  uploadés avec succès", "data", uploaded);
    }

    @Transactional
    public Map<String, Object> updateDossier(
            Integer dossierId, AuthenticatedUser user, Map<String, MultipartFile> files) {
        autorisationService.requireAutorisationToday(user.getUserRf());

        List<Map<String, Object>> dossierRows = jdbcTemplate.queryForList(
                "SELECT * FROM rf_dossier WHERE id = :id", Map.of("id", dossierId));
        if (dossierRows.isEmpty()) {
            throw new BusinessException(404, "Dossier non trouvé");
        }
        Map<String, Object> dossierFind = dossierRows.getFirst();
        int agentId = ((Number) dossierFind.get("id_op_agent")).intValue();

        List<Map<String, Object>> ancienRows = jdbcTemplate.queryForList(
                "SELECT * FROM rf_dossier WHERE id_op_agent = :agentId LIMIT 1", Map.of("agentId", agentId));
        Map<String, Object> ancien = ancienRows.isEmpty() ? null : ancienRows.getFirst();

        Map<String, String> uploaded = new HashMap<>();
        for (String field : FILE_FIELDS) {
            MultipartFile file = files.get(field);
            if (file != null && !file.isEmpty()) {
                if (ancien != null && ancien.get(field) != null) {
                    deleteStoredFile(String.valueOf(ancien.get(field)));
                }
                uploaded.put(field, storeDossierFile(file, field));
            } else if (ancien != null) {
                uploaded.put(field, ancien.get(field) == null ? null : String.valueOf(ancien.get(field)));
            } else {
                uploaded.put(field, null);
            }
        }

        Map<String, Object> result;
        if (ancien != null) {
            result = updateDossierDb(agentId, uploaded);
        } else {
            jdbcTemplate.update("""
                    INSERT INTO rf_dossier (id_op_agent, id_user_created, curiculum_vitae, diplome_etat,
                        diplome_gradua, diplome_licence, created_at, last_update)
                    VALUES (:agentId, :userCreated, :cv, :etat, :gradua, :licence, NOW(), NOW())
                    """, Map.of(
                    "agentId", agentId,
                    "userCreated", user.getUserRf(),
                    "cv", uploaded.get("curiculum_vitae"),
                    "etat", uploaded.get("diplome_etat"),
                    "gradua", uploaded.get("diplome_gradua"),
                    "licence", uploaded.get("diplome_licence")));
            result = Map.of("code", 200, "data", uploaded);
        }

        Object data = result.get("data");
        if (data instanceof Map<?, ?> map && map.containsKey("id")) {
            return Map.of("code", 200, "message", "Dossier de l'agent mis à jour avec succès.", "data", data);
        }
        List<Map<String, Object>> updated = jdbcTemplate.queryForList(
                "SELECT * FROM rf_dossier WHERE id_op_agent = :agentId", Map.of("agentId", agentId));
        return Map.of("code", 200, "message", "Dossier de l'agent mis à jour avec succès.",
                "data", updated.isEmpty() ? uploaded : updated.getFirst());
    }

    private Map<String, Object> updateDossierDb(int agentId, Map<String, String> uploaded) {
        int updated = jdbcTemplate.update("""
                UPDATE rf_dossier SET curiculum_vitae = :cv, diplome_etat = :etat,
                    diplome_gradua = :gradua, diplome_licence = :licence, last_update = NOW()
                WHERE id_op_agent = :agentId
                """, Map.of(
                "agentId", agentId,
                "cv", uploaded.get("curiculum_vitae"),
                "etat", uploaded.get("diplome_etat"),
                "gradua", uploaded.get("diplome_gradua"),
                "licence", uploaded.get("diplome_licence")));

        if (updated == 0) {
            throw new BusinessException(400, "Aucune modification détectée sur le dossier.");
        }
        Map<String, Object> row = jdbcTemplate.queryForList(
                "SELECT * FROM rf_dossier WHERE id_op_agent = :agentId LIMIT 1", Map.of("agentId", agentId)).getFirst();
        return Map.of("code", 200, "message", "Dossier mis à jour avec succès.", "data", row);
    }

    private Map<String, String> uploadFiles(Map<String, MultipartFile> files) {
        Map<String, String> uploaded = new HashMap<>();
        for (String field : FILE_FIELDS) {
            uploaded.put(field, storeDossierFile(files.get(field), field));
        }
        return uploaded;
    }

    private String storeDossierFile(MultipartFile file, String fieldName) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (file.getSize() > DOC_MAX) {
            throw new BusinessException(400, "Le fichier " + fieldName + " dépasse la taille maximale autorisée (5 Mo).");
        }
        String extension = extensionOf(file.getOriginalFilename());
        if (!DOC_EXT.contains(extension)) {
            throw new BusinessException(400, "Le fichier " + fieldName + " a une extension non autorisée.");
        }
        String mimeType = file.getContentType();
        if (mimeType == null || !DOC_MIME.contains(mimeType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException(400, "Le fichier " + fieldName + " doit être au format PDF, PNG ou JPEG.");
        }
        try {
            Path directory = Paths.get(uploadProperties.getDossiersAgentsDir()).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            String original = file.getOriginalFilename() == null ? "file" : Paths.get(file.getOriginalFilename()).getFileName().toString();
            String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + safeName;
            file.transferTo(directory.resolve(fileName));
            return uploadProperties.getPublicDossiersAgentsPath() + "/" + fileName;
        } catch (IOException ex) {
            throw new BusinessException(500, "Erreur lors de l'upload du fichier " + fieldName);
        }
    }

    private void deleteStoredFile(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return;
        }
        try {
            Path file = Paths.get(uploadProperties.getDossiersAgentsDir())
                    .toAbsolutePath().normalize()
                    .resolve(storedPath.substring(storedPath.lastIndexOf('/') + 1));
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
            // suppression best-effort
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
