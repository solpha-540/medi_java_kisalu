package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.config.UploadProperties;
import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** Absences et signalements (op_absence_signalement). */
@Service
public class AgentAbsenceService {

    private static final Pattern TIME = Pattern.compile("^(?:[01]\\d|2[0-3]):[0-5]\\d(?::[0-5]\\d)?$");
    private static final java.util.Set<String> DOC_EXT = java.util.Set.of("pdf", "jpg", "jpeg", "png");
    private static final java.util.Set<String> DOC_MIME = java.util.Set.of("application/pdf", "image/jpeg", "image/png");
    private static final long DOC_MAX = 1572864L;

    private static final String ABSENCE_SELECT = """
            SELECT abs.*, per.nom, per.post_nom, per.prenom,
                   CONCAT(per.nom, ' ', per.post_nom, ' ', per.prenom) AS nom_complet,
                   con.telephone_un, con.telephone_deux, con.telephone_trois, con.email,
                   srv.libelle AS service, dir.libele AS direction,
                   fct.libelle AS fonction, grd.libelle AS grade
            FROM op_absence_signalement abs
            LEFT JOIN op_agent ag ON ag.id = abs.agent_id
            LEFT JOIN rf_personnes per ON per.id = ag.personne_id
            LEFT JOIN rf_contact con ON con.id_op_agent = ag.id
            LEFT JOIN op_affectation_rh aff ON aff.agent_id = ag.id
            LEFT JOIN rf_service_rh srv ON srv.id = aff.service_id
            LEFT JOIN rf_direction dir ON dir.Id = srv.id_direction
            LEFT JOIN rf_fonction fct ON fct.id = ag.id_fonction
            LEFT JOIN rf_grade grd ON grd.id = ag.id_grade
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DocumentStorage documentStorage;
    private final UploadProperties uploadProperties;
    private final UpdateHistoryService updateHistoryService;

    public AgentAbsenceService(
            NamedParameterJdbcTemplate jdbcTemplate,
            DocumentStorage documentStorage,
            UploadProperties uploadProperties,
            UpdateHistoryService updateHistoryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.documentStorage = documentStorage;
        this.uploadProperties = uploadProperties;
        this.updateHistoryService = updateHistoryService;
    }

    @Transactional
    public Map<String, Object> registerAbsence(Map<String, String> form, MultipartFile document) {
        validateTime(form.get("heure_debut"), "heure_debut");
        validateTime(form.get("heure_fin"), "heure_fin");

        String commentaire = form.get("commentaire_rh");
        if (commentaire != null && commentaire.length() > 500) {
            throw new BusinessException(400, "Le commentaire RH ne doit pas dépasser 500 caractères.");
        }

        for (String field : List.of("agent_id", "type_signalement", "date_debut", "date_fin", "motif", "id_user_created_at")) {
            if (form.get(field) == null || form.get(field).isBlank()) {
                throw new BusinessException(400, field + " est requis.");
            }
        }

        LocalDate debut = parseDate(form.get("date_debut"));
        LocalDate fin = parseDate(form.get("date_fin"));
        if (debut.isAfter(fin)) {
            throw new BusinessException(400, "date_debut doit être ≤ date_fin.");
        }

        String docFilename = storeJustificatifFilename(document);

        Map<String, Object> params = new HashMap<>();
        params.put("agentId", Integer.parseInt(form.get("agent_id")));
        params.put("typeSignalement", form.get("type_signalement"));
        params.put("dateDebut", form.get("date_debut"));
        params.put("dateFin", form.get("date_fin"));
        params.put("heureDebut", blankToNull(form.get("heure_debut")));
        params.put("heureFin", blankToNull(form.get("heure_fin")));
        params.put("motif", form.get("motif"));
        params.put("document", docFilename);
        params.put("statut", form.getOrDefault("statut", "en_attente"));
        params.put("commentaireRh", blankToNull(commentaire));
        params.put("userCreated", form.get("id_user_created_at"));

        jdbcTemplate.update("""
                INSERT INTO op_absence_signalement (agent_id, type_signalement, date_debut, date_fin,
                    heure_debut, heure_fin, motif, document_justificatif, statut, commentaire_rh,
                    id_user_created_at, created_at, last_update)
                VALUES (:agentId, :typeSignalement, :dateDebut, :dateFin,
                    :heureDebut, :heureFin, :motif, :document, :statut, :commentaireRh,
                    :userCreated, NOW(), NOW())
                """, params);

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("agent_id", params.get("agentId"));
        data.put("type_signalement", params.get("typeSignalement"));
        data.put("date_debut", params.get("dateDebut"));
        data.put("date_fin", params.get("dateFin"));
        data.put("statut", params.get("statut"));
        if (docFilename != null) {
            data.put("document_url", "/public/documentJustificatif/" + docFilename);
        }

        return Map.of("code", 200, "message", "Signalement d'absence créé avec succès.", "data", data);
    }

    public Map<String, Object> getAllAbsences() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(ABSENCE_SELECT, Map.of());
        return Map.of("code", 200, "message", "Absences récupérées", "data", rows);
    }

    public Map<String, Object> getAbsenceByAgent(Integer agentId) {
        if (agentId == null) {
            throw new BusinessException(400, "agent_id est requis.");
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                ABSENCE_SELECT + " WHERE abs.agent_id = :agentId", Map.of("agentId", agentId));
        return Map.of("code", 200, "message", "Absences de l'agent", "data", rows);
    }

    public Map<String, Object> getAbsenceByService(Integer serviceId) {
        if (serviceId == null) {
            throw new BusinessException(400, "service_id est requis.");
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                ABSENCE_SELECT + " WHERE aff.service_id = :serviceId", Map.of("serviceId", serviceId));
        return Map.of("code", 200, "message", "Absences du service", "data", rows);
    }

    public Map<String, Object> getAbsenceByDate(String dateDebut, String dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new BusinessException(400, "date_debut et date_fin sont requis.");
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                ABSENCE_SELECT + " WHERE abs.date_debut >= :debut AND abs.date_fin <= :fin",
                Map.of("debut", dateDebut, "fin", dateFin));
        return Map.of("code", 200, "message", "Absences par période", "data", rows);
    }

    @Transactional
    public Map<String, Object> updateAbsenceStatus(Map<String, String> body, AuthenticatedUser user) {
        if (body.get("id") == null || body.get("statut") == null) {
            throw new BusinessException(400, "id et statut sont requis.");
        }

        int id = Integer.parseInt(body.get("id"));
        Map<String, Object> existing = findAbsenceById(id);

        String statut = body.get("statut");
        String commentaire = blankToNull(body.get("commentaire_rh"));

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("statut", statut);
        params.put("commentaire", commentaire);

        jdbcTemplate.update("""
                UPDATE op_absence_signalement
                SET statut = :statut, commentaire_rh = :commentaire, last_update = NOW()
                WHERE id = :id
                """, params);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("statut", statut);
        updateData.put("commentaire_rh", commentaire);
        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("agent_id")).intValue(),
                "op_absence_signalement",
                id,
                user.getUserRf(),
                existing,
                updateData);

        return Map.of("code", 200, "message", "Statut mis à jour avec succès.",
                "data", Map.of("id", id, "statut", statut));
    }

    @Transactional
    public Map<String, Object> updateAbsence(Map<String, String> form, MultipartFile document) {
        if (form.get("id") == null || form.get("id").isBlank()) {
            throw new BusinessException(400, "Le champ id est requis pour la mise à jour.");
        }

        validateTime(form.get("heure_debut"), "heure_debut");
        validateTime(form.get("heure_fin"), "heure_fin");

        String commentaire = form.get("commentaire_rh");
        if (commentaire != null && commentaire.length() > 500) {
            throw new BusinessException(400, "Le commentaire RH ne doit pas dépasser 500 caractères.");
        }

        for (String field : List.of("agent_id", "type_signalement", "date_debut", "date_fin", "motif", "id_user_created_at")) {
            if (form.get(field) == null || form.get(field).isBlank()) {
                throw new BusinessException(400,
                        "id, agent_id, type_signalement, date_debut, date_fin, motif et id_user_created_at sont requis.");
            }
        }

        LocalDate debut = parseDate(form.get("date_debut"));
        LocalDate fin = parseDate(form.get("date_fin"));
        if (debut.isAfter(fin)) {
            throw new BusinessException(400, "date_debut doit être ≤ date_fin.");
        }

        int id = Integer.parseInt(form.get("id"));
        Map<String, Object> existing = findAbsenceById(id);

        String docFilename = storeJustificatifFilename(document);
        if (docFilename == null && existing.get("document_justificatif") != null) {
            docFilename = String.valueOf(existing.get("document_justificatif"));
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("agentId", Integer.parseInt(form.get("agent_id")));
        params.put("typeSignalement", form.get("type_signalement"));
        params.put("dateDebut", form.get("date_debut"));
        params.put("dateFin", form.get("date_fin"));
        params.put("heureDebut", blankToNull(form.get("heure_debut")));
        params.put("heureFin", blankToNull(form.get("heure_fin")));
        params.put("motif", form.get("motif"));
        params.put("document", docFilename);
        params.put("commentaireRh", blankToNull(commentaire));

        jdbcTemplate.update("""
                UPDATE op_absence_signalement
                SET agent_id = :agentId, type_signalement = :typeSignalement,
                    date_debut = :dateDebut, date_fin = :dateFin,
                    heure_debut = :heureDebut, heure_fin = :heureFin,
                    motif = :motif, document_justificatif = :document,
                    commentaire_rh = :commentaireRh, last_update = NOW()
                WHERE id = :id
                """, params);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        if (document != null && !document.isEmpty() && docFilename != null) {
            data.put("document_url", "/public/documentJustificatif/" + docFilename);
        }

        return Map.of("code", 200, "message", "Signalement mis à jour avec succès.", "data", data);
    }

    public Map<String, Object> getAbsenceSignalementById(String idUserCreatedAt) {
        if (idUserCreatedAt == null || idUserCreatedAt.isBlank()) {
            throw new BusinessException(400, "id_user_created_at est requis.");
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT * FROM op_absence_signalement
                WHERE id_user_created_at = :userCreated
                ORDER BY created_at DESC
                """, Map.of("userCreated", idUserCreatedAt));
        return Map.of("code", 200, "message", "Signalement(s) récupéré(s) avec succès", "data", rows);
    }

    @Transactional
    public Map<String, Object> updateAbsenceSignalement(
            Integer signalementId, Map<String, String> body, AuthenticatedUser user) {
        if (signalementId == null) {
            throw new BusinessException(400, "Id du signalement est requis");
        }
        String statut = body.get("statut");
        String commentaire = body.get("commentaire_rh");
        if (statut == null || statut.isBlank() || commentaire == null || commentaire.isBlank()) {
            throw new BusinessException(400, "Les champs statut et commentaire_rh sont obligatoires");
        }
        if (commentaire.length() > 150) {
            throw new BusinessException(400, "Le commentaire RH ne doit pas dépasser 150 caractères");
        }

        Map<String, Object> existing = findAbsenceById(signalementId);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("statut", statut);
        updateData.put("commentaire_rh", commentaire);
        updateData.put("id_user_updated_at", user.getUserRf());

        jdbcTemplate.update("""
                UPDATE op_absence_signalement
                SET statut = :statut, commentaire_rh = :commentaire,
                    id_user_updated_at = :userUpdated, last_update = NOW()
                WHERE id = :id
                """, Map.of(
                "statut", statut,
                "commentaire", commentaire,
                "userUpdated", user.getUserRf(),
                "id", signalementId));

        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("agent_id")).intValue(),
                "op_absence_signalement",
                signalementId,
                user.getUserRf(),
                existing,
                updateData);

        Map<String, Object> updated = jdbcTemplate.queryForList(
                "SELECT * FROM op_absence_signalement WHERE id = :id", Map.of("id", signalementId)).getFirst();

        return Map.of("code", 200, "message", "Signalement mis à jour avec succès", "data", updated);
    }

    private Map<String, Object> findAbsenceById(int id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM op_absence_signalement WHERE id = :id", Map.of("id", id));
        if (rows.isEmpty()) {
            throw new BusinessException(404, "Signalement non trouvé");
        }
        return rows.getFirst();
    }

    private String storeJustificatifFilename(MultipartFile document) {
        if (document == null || document.isEmpty()) {
            return null;
        }
        String stored = documentStorage.store(
                document, "document_justificatif",
                uploadProperties.getDocumentJustificatifDir(),
                uploadProperties.getPublicDocumentJustificatifPath(),
                "Tac-Doc_", DOC_MAX, DOC_EXT, DOC_MIME);
        int slash = Math.max(stored.lastIndexOf('/'), stored.lastIndexOf('\\'));
        return slash >= 0 ? stored.substring(slash + 1) : stored;
    }

    private void validateTime(String value, String field) {
        if (value != null && !value.isBlank() && !TIME.matcher(value).matches()) {
            throw new BusinessException(400, "Format de " + field + " invalide. Format accepté : HH:MM ou HH:MM:SS.");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(400, "Format de date invalide.");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
