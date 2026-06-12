package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.config.UploadProperties;
import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.common.util.AgentFormValidator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Routes agent etendues : update, conjoint, enfant, formation agent. */
@Service
public class AgentExtendedService {

    private static final Set<String> DOC_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");
    private static final Set<String> DOC_MIME = Set.of("application/pdf", "image/jpeg", "image/png");
    private static final long DOC_MAX = 2 * 1024 * 1024L;
    private static final Set<String> FORMATION_STATUTS = Set.of("active", "pending", "autorise", "rejete");
    private static final String FORMATION_JOIN = """
            SELECT f.*, a.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom
            FROM rf_formation f
            JOIN op_agent a ON f.id_op_agent = a.id
            JOIN rf_personnes p ON a.personne_id = p.id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleAuthorizationService roleAuthorizationService;
    private final AutorisationService autorisationService;
    private final UpdateHistoryService updateHistoryService;
    private final AgentPhotoStorage photoStorage;
    private final DocumentStorage documentStorage;
    private final UploadProperties uploadProperties;

    public AgentExtendedService(
            NamedParameterJdbcTemplate jdbcTemplate,
            RoleAuthorizationService roleAuthorizationService,
            AutorisationService autorisationService,
            UpdateHistoryService updateHistoryService,
            AgentPhotoStorage photoStorage,
            DocumentStorage documentStorage,
            UploadProperties uploadProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleAuthorizationService = roleAuthorizationService;
        this.autorisationService = autorisationService;
        this.updateHistoryService = updateHistoryService;
        this.photoStorage = photoStorage;
        this.documentStorage = documentStorage;
        this.uploadProperties = uploadProperties;
    }

    @Transactional
    public Map<String, Object> updateAgent(Integer id, Map<String, String> form, MultipartFile photo, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef personnel");
        if (id == null || id <= 0) {
            throw new BusinessException(400, "L'ID de l'agent est requis pour la mise à jour.");
        }

        List<Map<String, Object>> agentRows = jdbcTemplate.queryForList(
                "SELECT id, personne_id AS personneId FROM op_agent WHERE id = :id", Map.of("id", id));
        if (agentRows.isEmpty()) {
            throw new BusinessException(404, "Agent introuvable pour l'ID fourni.");
        }
        Integer personneId = ((Number) agentRows.getFirst().get("personneId")).intValue();

        Map<String, String> updates = new HashMap<>();
        form.forEach((k, v) -> {
            if (v != null && !v.isBlank()) {
                updates.put(k, v.trim());
            }
        });

        if (photo != null && !photo.isEmpty()) {
            updates.put("photo", photoStorage.storeAgentPhoto(photo));
        }
        if (updates.isEmpty()) {
            throw new BusinessException(400, "Aucun champ valide à mettre à jour.");
        }

        updatePersonne(personneId, updates);
        updateOpAgent(id, updates);
        updateContact(id, updates);

        return Map.of("code", 200, "message", "Agent mis à jour avec succès.", "data", updates);
    }

    public Map<String, Object> usersCountByRole() {
        List<Map<String, Object>> data = jdbcTemplate.queryForList("""
                SELECT r.libelle AS role, COUNT(ur.id_users) AS nombre_utilisateurs
                FROM sys_roles r
                LEFT JOIN sys_user_roles ur ON ur.id_role = r.id
                LEFT JOIN sys_users u ON u.id = ur.id_users
                GROUP BY r.libelle
                """, Map.of());
        return Map.of("code", 200, "message", "Nombre d'utilisateurs par rôle récupéré avec succès", "data", data);
    }

    @Transactional
    public Map<String, Object> registerConjoint(Map<String, String> form, MultipartFile acteMariage, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef suivi carriere");

        String nom = AgentFormValidator.requireNonBlank("nom", form.get("nom"));
        String postNom = AgentFormValidator.requireNonBlank("post_nom", form.get("post_nom"));
        String prenom = AgentFormValidator.requireNonBlank("prenom", form.get("prenom"));
        AgentFormValidator.validateIdentityField("nom", nom);
        AgentFormValidator.validateIdentityField("post_nom", postNom);
        AgentFormValidator.validateIdentityField("prenom", prenom);

        String telephone = AgentFormValidator.normalizePhone(AgentFormValidator.requireNonBlank("telephone", form.get("telephone")));
        Integer agentId = Integer.parseInt(AgentFormValidator.requireNonBlank("id_op_agent", form.get("id_op_agent")));

        String actePath = documentStorage.store(
                acteMariage, "acte_mariage",
                uploadProperties.getActesMariageDir(), uploadProperties.getPublicActesMariagePath(),
                "acte_mariage_", DOC_MAX, DOC_EXTENSIONS, DOC_MIME);

        Map<String, Object> params = new HashMap<>();
        params.put("nom", titleCase(nom));
        params.put("postnom", titleCase(postNom));
        params.put("prenom", titleCase(prenom));
        params.put("sexe", form.get("sexe"));
        params.put("dateNaissance", form.get("date_naissance"));
        params.put("lieuNaissance", form.get("lieu_naissance"));
        params.put("telephone", telephone);
        params.put("acteMariage", actePath);
        params.put("agentId", agentId);
        params.put("userCreated", user.getUserRf());

        jdbcTemplate.update("""
                INSERT INTO rf_conjoint (nom, postnom, prenom, sexe, date_naissance, lieu_naissance,
                    telephone, acte_mariage, id_op_agent, id_user_created, created_at, last_update)
                VALUES (:nom, :postnom, :prenom, :sexe, :dateNaissance, :lieuNaissance,
                    :telephone, :acteMariage, :agentId, :userCreated, NOW(), NOW())
                """, params);

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        List<Map<String, Object>> row = jdbcTemplate.queryForList("SELECT * FROM rf_conjoint WHERE id = :id", Map.of("id", id));
        return Map.of("code", 200, "message", "Conjoint enregistré avec succès", "data", row.getFirst());
    }

    public Map<String, Object> listConjoints(Integer id) {
        if (id != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_conjoint WHERE id = :id", Map.of("id", id));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Conjoint introuvable");
            }
            return Map.of("code", 200, "message", "Conjoint trouvé", "data", rows.getFirst());
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_conjoint", Map.of());
        return Map.of("code", 200, "message", "Liste des conjoints", "data", rows);
    }

    @Transactional
    public Map<String, Object> registerEnfant(Map<String, String> form, MultipartFile attestation, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef suivi carriere");

        String nom = AgentFormValidator.requireNonBlank("nom", form.get("nom"));
        String postnom = AgentFormValidator.requireNonBlank("postnom", form.get("postnom"));
        String prenom = AgentFormValidator.requireNonBlank("prenom", form.get("prenom"));
        AgentFormValidator.validateIdentityField("nom", nom);
        AgentFormValidator.validateIdentityField("postnom", postnom);
        AgentFormValidator.validateIdentityField("prenom", prenom);

        Integer agentId = Integer.parseInt(AgentFormValidator.requireNonBlank("id_agent", form.get("id_agent")));
        String attestationPath = documentStorage.store(
                attestation, "attestation_naissance",
                uploadProperties.getAttestationsDir(), uploadProperties.getPublicAttestationsPath(),
                "attestation_", DOC_MAX, DOC_EXTENSIONS, DOC_MIME);

        Map<String, Object> params = new HashMap<>();
        params.put("nom", titleCase(nom));
        params.put("postnom", titleCase(postnom));
        params.put("prenom", titleCase(prenom));
        params.put("sexe", form.get("sexe"));
        params.put("dateNaissance", form.get("date_naissance"));
        params.put("agentId", agentId);
        params.put("attestation", attestationPath);
        params.put("userCreated", user.getUserRf());

        jdbcTemplate.update("""
                INSERT INTO rf_employer_enfant (nom, postnom, prenom, sexe, date_naissance,
                    id_agent, attestation_naissance, id_user_created, created_at, last_update)
                VALUES (:nom, :postnom, :prenom, :sexe, :dateNaissance,
                    :agentId, :attestation, :userCreated, NOW(), NOW())
                """, params);

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        List<Map<String, Object>> row = jdbcTemplate.queryForList("SELECT * FROM rf_employer_enfant WHERE id = :id", Map.of("id", id));
        return Map.of("code", 200, "message", "Enfant enregistré avec succès", "data", row.getFirst());
    }

    public Map<String, Object> listEnfants(Integer id) {
        if (id != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM rf_employer_enfant WHERE id = :id", Map.of("id", id));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Enfant introuvable");
            }
            return Map.of("code", 200, "message", "Enfant trouvé", "data", rows.getFirst());
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_employer_enfant", Map.of());
        return Map.of("code", 200, "message", "Liste des enfants", "data", rows);
    }

    @Transactional
    public Map<String, Object> registerFormation(Map<String, String> form, MultipartFile document, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef suivi carriere", "chef formation");

        Integer agentId = Integer.parseInt(AgentFormValidator.requireNonBlank("id_op_agent", form.get("id_op_agent")));
        String docPath = documentStorage.store(
                document, "document",
                uploadProperties.getDocumentsFormationsDir(), uploadProperties.getPublicDocumentsFormationsPath(),
                "formation_", DOC_MAX, DOC_EXTENSIONS, DOC_MIME);

        Map<String, Object> params = new HashMap<>();
        params.put("nomFormation", form.get("nom_formation").trim());
        params.put("etablissement", form.get("etablissement").trim());
        params.put("experience", form.get("experience").trim());
        params.put("annee", form.get("annee"));
        params.put("document", docPath);
        params.put("agentId", agentId);
        params.put("statut", form.getOrDefault("statut", "active"));
        params.put("commentaire", form.get("commentaire"));
        params.put("userCreated", user.getUserRf());

        jdbcTemplate.update("""
                INSERT INTO rf_formation (nom_formation, etablissement, experience, annee, document,
                    id_op_agent, statut, commentaire, id_user_created, created_at, last_update)
                VALUES (:nomFormation, :etablissement, :experience, :annee, :document,
                    :agentId, :statut, :commentaire, :userCreated, NOW(), NOW())
                """, params);

        jdbcTemplate.update("UPDATE op_agent SET statut = 2 WHERE id = :id", Map.of("id", agentId));

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        List<Map<String, Object>> row = jdbcTemplate.queryForList("""
                SELECT f.*, a.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom
                FROM rf_formation f
                JOIN op_agent a ON f.id_op_agent = a.id
                JOIN rf_personnes p ON a.personne_id = p.id
                WHERE f.id = :id
                """, Map.of("id", id));

        return Map.of("code", 200, "message", "Formation créée avec succès.", "data", row.getFirst());
    }

    public Map<String, Object> listFormations(Integer id) {
        if (id != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_formation WHERE id = :id", Map.of("id", id));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Formation introuvable");
            }
            return Map.of("code", 200, "message", "Formation trouvée", "data", rows.getFirst());
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_formation", Map.of());
        return Map.of("code", 200, "message", "Liste des formations", "data", rows);
    }

    @Transactional
    public Map<String, Object> updateConjoint(Integer conjointId, Map<String, String> form, AuthenticatedUser user) {
        autorisationService.requireAutorisationToday(user.getUserRf());

        Map<String, Object> existing = findRow("rf_conjoint", conjointId, "Conjoint non trouvé");
        Map<String, Object> updateData = extractUpdate(form, List.of(
                "nom", "postnom", "prenom", "sexe", "id_op_agent", "date_naissance", "lieu_naissance", "telephone", "acte_mariage"));
        if (updateData.isEmpty()) {
            throw new BusinessException(400, "Aucune donnée valide fournie");
        }

        applyPartialUpdate("rf_conjoint", conjointId, updateData, "last_update");
        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("id_op_agent")).intValue(),
                "rf_conjoint", conjointId, user.getUserRf(), existing, updateData);

        Map<String, Object> updated = jdbcTemplate.queryForList(
                "SELECT * FROM rf_conjoint WHERE id = :id", Map.of("id", conjointId)).getFirst();
        return Map.of("code", 200, "message", "Les informations ont été mises à jour", "data", updated);
    }

    @Transactional
    public Map<String, Object> deleteConjoint(Integer id, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef suivi carriere");
        if (id == null || id <= 0) {
            throw new BusinessException(400, "ID conjoint invalide ou manquant");
        }

        Map<String, Object> existing = findRow("rf_conjoint", id, "Conjoint introuvable");
        deleteStoredFile((String) existing.get("acte_mariage"), uploadProperties.getActesMariageDir());
        jdbcTemplate.update("DELETE FROM rf_conjoint WHERE id = :id", Map.of("id", id));
        return Map.of("code", 200, "message", "Conjoint supprimé avec succès", "data", existing);
    }

    @Transactional
    public Map<String, Object> updateEnfant(Integer enfantId, Map<String, String> form, AuthenticatedUser user) {
        autorisationService.requireAutorisationToday(user.getUserRf());

        Map<String, Object> existing = findRow("rf_employer_enfant", enfantId, "Enfant non trouvé");
        Map<String, Object> updateData = extractUpdate(form, List.of(
                "nom", "postnom", "prenom", "sexe", "id_agent", "date_naissance", "attestation_naissance"));
        if (updateData.isEmpty()) {
            throw new BusinessException(400, "Aucune donnée valide fournie");
        }

        applyPartialUpdate("rf_employer_enfant", enfantId, updateData, "last_update");
        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("id_agent")).intValue(),
                "rf_employer_enfant", enfantId, user.getUserRf(), existing, updateData);

        Map<String, Object> updated = jdbcTemplate.queryForList(
                "SELECT * FROM rf_employer_enfant WHERE id = :id", Map.of("id", enfantId)).getFirst();
        return Map.of("code", 200, "message", "Les informations ont été mises à jour", "data", updated);
    }

    @Transactional
    public Map<String, Object> deleteEnfant(Integer id, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef suivi carriere");
        if (id == null || id <= 0) {
            throw new BusinessException(400, "ID enfant invalide ou manquant");
        }

        Map<String, Object> existing = findRow("rf_employer_enfant", id, "Enfant introuvable");
        deleteStoredFile((String) existing.get("attestation_naissance"), uploadProperties.getAttestationsDir());
        jdbcTemplate.update("DELETE FROM rf_employer_enfant WHERE id = :id", Map.of("id", id));
        return Map.of("code", 200, "message", "Enfant supprimé avec succès", "data", existing);
    }

    @Transactional
    public Map<String, Object> updateFormation(Integer formationId, Map<String, String> form, AuthenticatedUser user) {
        autorisationService.requireAutorisationToday(user.getUserRf());

        Map<String, Object> existing = findRow("rf_formation", formationId, "Formation non trouvée");
        Map<String, Object> updateData = extractUpdate(form, List.of(
                "nom_formation", "experience", "etablissement", "annee", "document", "id_op_agent", "statut", "status"));
        if (updateData.containsKey("status")) {
            updateData.put("statut", updateData.remove("status"));
        }
        if (updateData.isEmpty()) {
            throw new BusinessException(400, "Aucune donnée valide fournie");
        }

        applyPartialUpdate("rf_formation", formationId, updateData, "last_update");
        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("id_op_agent")).intValue(),
                "rf_formation", formationId, user.getUserRf(), existing, updateData);

        Map<String, Object> updated = jdbcTemplate.queryForList(
                "SELECT * FROM rf_formation WHERE id = :id", Map.of("id", formationId)).getFirst();
        return Map.of("code", 200, "message", "Les informations ont été mises à jour", "data", updated);
    }

    public Map<String, Object> listFormationsByStatut(String statut) {
        if (statut != null && !statut.isBlank()) {
            String normalized = statut.trim().toLowerCase(Locale.ROOT);
            if (!FORMATION_STATUTS.contains(normalized)) {
                throw new BusinessException(400,
                        "Statut invalide. Valeurs autorisées : " + String.join(", ", FORMATION_STATUTS));
            }
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    FORMATION_JOIN + " WHERE f.statut = :statut", Map.of("statut", normalized));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Aucune formation trouvée pour le statut '" + normalized + "'");
            }
            return Map.of("code", 200, "message", "Formations trouvées pour le statut '" + normalized + "'", "data", rows);
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FORMATION_JOIN, Map.of());
        return Map.of("code", 200, "message", "Formations récupérées avec succès.", "data", rows);
    }

    @Transactional
    public Map<String, Object> decisionFormation(Map<String, Object> body, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "dg", "drh");

        int idFormation = body.get("id_formation") instanceof Number n ? n.intValue()
                : Integer.parseInt(String.valueOf(body.getOrDefault("id_formation", "0")));
        String statut = String.valueOf(body.getOrDefault("statut", "")).trim().toLowerCase(Locale.ROOT);

        if (idFormation <= 0) {
            throw new BusinessException(400, "ID formation invalide");
        }
        if (!Set.of("autorise", "rejete").contains(statut)) {
            throw new BusinessException(400, "Décision invalide. Cet valeur n'est pas reconnue.");
        }

        List<Map<String, Object>> pending = jdbcTemplate.queryForList(
                FORMATION_JOIN + " WHERE f.statut = 'pending'", Map.of());
        if (pending.isEmpty()) {
            throw new BusinessException(404, "Aucune formation en pending à traiter");
        }

        boolean found = pending.stream().anyMatch(row -> idFormation == ((Number) row.get("id")).intValue());
        if (!found) {
            throw new BusinessException(400, "Cette formation n'est pas en statut pending");
        }

        String commentaire = body.get("commentaire") != null ? String.valueOf(body.get("commentaire")) : null;
        Map<String, Object> params = new HashMap<>();
        params.put("statut", statut);
        params.put("commentaire", commentaire);
        params.put("id", idFormation);
        jdbcTemplate.update("""
                UPDATE rf_formation SET statut = :statut, commentaire = :commentaire, last_update = NOW()
                WHERE id = :id
                """, params);

        Map<String, Object> data = new HashMap<>();
        data.put("id", idFormation);
        data.put("statut", statut);
        data.put("commentaire", commentaire);
        return Map.of("code", 200, "message", "Statut de la formation mis à jour avec succès.", "data", data);
    }

    private void updatePersonne(Integer personneId, Map<String, String> updates) {
        List<String> fields = List.of("nom", "post_nom", "prenom", "sexe", "photo", "mail", "quartier", "avenue", "num_parcelle");
        applyUpdate("rf_personnes", "id", personneId, fields, updates, Map.of("post_nom", "post_nom", "num_parcelle", "num_parcelle"));
    }

    private void updateOpAgent(Integer agentId, Map<String, String> updates) {
        List<String> fields = List.of("id_fonction", "id_grade", "date_embauche", "contrat", "date_naissance",
                "domaine_etude", "etat_civil", "statut", "code_generate_tac", "matricule");
        applyUpdate("op_agent", "id", agentId, fields, updates, Map.of());
    }

    private void updateContact(Integer agentId, Map<String, String> updates) {
        if (updates.containsKey("mail")) {
            updates.put("email", updates.get("mail"));
        }
        List<String> fields = List.of("telephone_un", "telephone_deux", "telephone_trois", "email");
        applyUpdate("rf_contact", "id_op_agent", agentId, fields, updates, Map.of());
    }

    private void applyUpdate(String table, String idColumn, Integer id, List<String> fields,
                             Map<String, String> updates, Map<String, String> columnAliases) {
        List<String> sets = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        for (String field : fields) {
            if (updates.containsKey(field)) {
                String column = columnAliases.getOrDefault(field, field);
                sets.add(column + " = :" + field);
                params.put(field, updates.get(field));
            }
        }
        if (sets.isEmpty()) {
            return;
        }
        sets.add("last_update = NOW()");
        String sql = "UPDATE " + table + " SET " + String.join(", ", sets) + " WHERE " + idColumn + " = :id";
        jdbcTemplate.update(sql, params);
    }

    private String titleCase(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        String lower = value.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private Map<String, Object> findRow(String table, int id, String notFoundMessage) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM " + table + " WHERE id = :id", Map.of("id", id));
        if (rows.isEmpty()) {
            throw new BusinessException(404, notFoundMessage);
        }
        return rows.getFirst();
    }

    private Map<String, Object> extractUpdate(Map<String, String> form, List<String> allowedFields) {
        Map<String, Object> updateData = new HashMap<>();
        for (String field : allowedFields) {
            if (form.containsKey(field) && form.get(field) != null && !form.get(field).isBlank()) {
                updateData.put(field, form.get(field).trim());
            }
        }
        return updateData;
    }

    private void applyPartialUpdate(String table, int id, Map<String, Object> updateData, String timestampColumn) {
        List<String> sets = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        updateData.forEach((column, value) -> {
            sets.add(column + " = :" + column);
            params.put(column, value);
        });
        sets.add(timestampColumn + " = NOW()");
        jdbcTemplate.update("UPDATE " + table + " SET " + String.join(", ", sets) + " WHERE id = :id", params);
    }

    private void deleteStoredFile(String storedPath, String storageDir) {
        if (storedPath == null || storedPath.isBlank()) {
            return;
        }
        String filename = storedPath.substring(storedPath.lastIndexOf('/') + 1);
        Path path = Paths.get(storageDir).toAbsolutePath().normalize().resolve(filename);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new BusinessException(500, "Impossible de supprimer le fichier d'attestation");
        }
    }
}
