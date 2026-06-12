package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Affectation agent (AssignAgent, ReassignAgent — OpAffectationRhSerializer PHP). */
@Service
public class AgentAffectationService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UpdateHistoryService updateHistoryService;

    public AgentAffectationService(
            NamedParameterJdbcTemplate jdbcTemplate,
            UpdateHistoryService updateHistoryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.updateHistoryService = updateHistoryService;
    }

    @Transactional
    public Map<String, Object> assignAgent(Map<String, Object> body) {
        int agentId = requireInt(body, "agent_id");
        String userCreated = requireString(body, "Id_user_created_at");
        Integer serviceId = optionalInt(body, "service_id");
        Integer fonctionId = optionalInt(body, "fonction_id");
        Integer gradeId = optionalInt(body, "grade_id");

        if (!agentExists(agentId)) {
            throw new BusinessException(400, "L'agent n'existe pas.");
        }
        if (!userExistsByIdGenerate(userCreated)) {
            throw new BusinessException(400, "Utilisateur créateur invalide.");
        }

        String libelle = null;
        String codeClassification = null;
        if (gradeId != null) {
            List<Map<String, Object>> gradeRows = jdbcTemplate.queryForList(
                    "SELECT libelle, code_classification FROM rf_grade WHERE id = :id LIMIT 1",
                    Map.of("id", gradeId));
            if (!gradeRows.isEmpty()) {
                libelle = String.valueOf(gradeRows.getFirst().get("libelle")).trim().toUpperCase(Locale.ROOT);
                Object cc = gradeRows.getFirst().get("code_classification");
                codeClassification = cc == null ? null : String.valueOf(cc);
            }
        }

        Integer adresseId = jdbcTemplate.query(
                """
                        SELECT p.adresse_id FROM op_agent a
                        JOIN rf_personnes p ON p.id = a.personne_id
                        WHERE a.id = :agentId LIMIT 1
                        """,
                Map.of("agentId", agentId),
                rs -> rs.next() ? rs.getInt("adresse_id") : null);

        Map<String, Object> location = jdbcTemplate.queryForList("""
                SELECT ag.date_embauche, vil.CodeVille
                FROM op_agent ag
                LEFT JOIN op_adresse adr ON adr.Id = :adresseId
                LEFT JOIN rf_ville vil ON vil.Id = adr.ville_id
                WHERE ag.id = :agentId LIMIT 1
                """, Map.of("adresseId", adresseId, "agentId", agentId)).stream().findFirst().orElse(Map.of());

        String anneeEmbauche = null;
        Object dateEmbauche = location.get("date_embauche");
        if (dateEmbauche != null) {
            LocalDate d = dateEmbauche instanceof java.sql.Date sqlDate
                    ? sqlDate.toLocalDate()
                    : LocalDate.parse(String.valueOf(dateEmbauche).substring(0, 10));
            anneeEmbauche = d.format(DateTimeFormatter.ofPattern("yy"));
        }

        String codeVille = location.get("CodeVille") == null ? null : String.valueOf(location.get("CodeVille"));
        String matriculeFinal = null;
        if (anneeEmbauche != null && codeClassification != null && codeVille != null) {
            matriculeFinal = "TAC" + anneeEmbauche + codeClassification + agentId + codeVille;
        }
        if (matriculeFinal == null) {
            throw new BusinessException(400, "Impossible de générer le matricule (données incomplètes).");
        }

        jdbcTemplate.update(
                "UPDATE op_agent SET matricule = :matricule, id_fonction = :fonction, id_grade = :grade WHERE id = :agentId",
                Map.of("matricule", matriculeFinal, "fonction", fonctionId, "grade", gradeId, "agentId", agentId));

        if (fonctionId != null) {
            List<Map<String, Object>> fonctionRows = jdbcTemplate.queryForList(
                    "SELECT libelle, id_service FROM rf_fonction WHERE id = :id LIMIT 1", Map.of("id", fonctionId));
            if (!fonctionRows.isEmpty()) {
                String fonctionLibelle = String.valueOf(fonctionRows.getFirst().get("libelle")).trim();
                if (fonctionLibelle.toLowerCase(Locale.ROOT).startsWith("chef")) {
                    jdbcTemplate.update(
                            "UPDATE rf_service_rh SET id_chefService = :agentId WHERE id = :serviceId",
                            Map.of("agentId", agentId, "serviceId", fonctionRows.getFirst().get("id_service")));
                }
            }
        }

        if ("CMD".equals(libelle)) {
            if (serviceId == null) {
                throw new BusinessException(400, "service_id requis pour un CMD.");
            }
            Object directionId = jdbcTemplate.queryForList(
                    "SELECT id_direction FROM rf_service_rh WHERE id = :id LIMIT 1",
                    Map.of("id", serviceId)).stream().findFirst().map(r -> r.get("id_direction")).orElse(null);
            if (directionId != null) {
                jdbcTemplate.update(
                        "UPDATE rf_direction SET id_directeur = :agentId WHERE Id = :directionId",
                        Map.of("agentId", agentId, "directionId", directionId));
            }
            if (affectationExists(agentId)) {
                throw new BusinessException(400, "Une affectation existe déjà pour cet agent.");
            }
            insertAffectation(agentId, serviceId, userCreated);
            return Map.of("code", 200, "message", "Agent CMD mis à jour avec succès.",
                    "data", Map.of("agent_id", agentId, "matricule", matriculeFinal));
        }

        if (serviceId == null) {
            throw new BusinessException(400, "service_id requis pour une affectation non CMD.");
        }
        if (!serviceExists(serviceId)) {
            throw new BusinessException(400, "Service invalide.");
        }
        if (affectationExists(agentId)) {
            throw new BusinessException(400, "Une affectation existe déjà pour cet agent.");
        }

        insertAffectation(agentId, serviceId, userCreated);
        Integer affectationId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        saveAffectationHistory(agentId, serviceId, userCreated);

        Map<String, Object> data = new HashMap<>();
        data.put("affectation_id", affectationId);
        data.put("agent_id", agentId);
        data.put("service_id", serviceId);
        data.put("adresse_id", adresseId);
        data.put("matricule", matriculeFinal);
        return Map.of("code", 200, "message", "Affectation créée avec succès.", "data", data);
    }

    @Transactional
    public Map<String, Object> reassignAgent(Map<String, Object> body, AuthenticatedUser user) {
        int agentId = requireInt(body, "agent_id");
        int serviceId = requireInt(body, "service_id");
        String userCreated = requireString(body, "Id_user_created_at");
        Integer fonctionId = optionalInt(body, "fonction_id");
        Integer gradeId = optionalInt(body, "grade_id");

        if (!agentExists(agentId)) {
            throw new BusinessException(404, "L'ID agent fourni n'existe pas.");
        }
        if (!serviceExists(serviceId)) {
            throw new BusinessException(404, "L'ID service fourni n'existe pas.");
        }
        if (!userExistsByIdGenerate(userCreated)) {
            throw new BusinessException(404, "Id_user_created_at invalide.");
        }

        List<Map<String, Object>> existingRows = jdbcTemplate.queryForList(
                "SELECT * FROM op_affectation_rh WHERE agent_id = :agentId LIMIT 1", Map.of("agentId", agentId));
        if (existingRows.isEmpty()) {
            throw new BusinessException(404, "Aucune affectation trouvée pour cet agent.");
        }
        Map<String, Object> existing = existingRows.getFirst();
        int oldServiceId = ((Number) existing.get("service_id")).intValue();

        saveAffectationHistory(agentId, oldServiceId, userCreated);
        jdbcTemplate.update(
                "UPDATE op_affectation_rh SET service_id = :serviceId, last_update = NOW() WHERE agent_id = :agentId",
                Map.of("serviceId", serviceId, "agentId", agentId));

        if (fonctionId != null) {
            jdbcTemplate.update(
                    "UPDATE op_agent SET id_fonction = :fonction, id_grade = :grade WHERE id = :agentId",
                    Map.of("fonction", fonctionId, "grade", gradeId, "agentId", agentId));
        }

        Map<String, Object> updateData = Map.of("service_id", serviceId);
        updateHistoryService.fillUpdateHistory(
                agentId, "op_affectation_rh",
                ((Number) existing.get("id")).intValue(),
                user.getUserRf(), existing, updateData);

        return Map.of("code", 200, "message", "Affectation mise à jour avec succès.",
                "data", Map.of("agent_id", agentId, "service_id", serviceId, "fonction_id", fonctionId));
    }

    private void insertAffectation(int agentId, int serviceId, String userCreated) {
        jdbcTemplate.update("""
                INSERT INTO op_affectation_rh (agent_id, service_id, Id_user_created_at, created_at, last_update)
                VALUES (:agentId, :serviceId, :userCreated, NOW(), NOW())
                """, Map.of("agentId", agentId, "serviceId", serviceId, "userCreated", userCreated));
    }

    private void saveAffectationHistory(int agentId, int serviceId, String userCreated) {
        jdbcTemplate.update("""
                INSERT INTO op_affectation_rh_historique (agent_id, service_id, Id_user_created_at)
                VALUES (:agentId, :serviceId, :userCreated)
                """, Map.of("agentId", agentId, "serviceId", serviceId, "userCreated", userCreated));
    }

    private boolean agentExists(int agentId) {
        return !jdbcTemplate.queryForList("SELECT id FROM op_agent WHERE id = :id", Map.of("id", agentId)).isEmpty();
    }

    private boolean serviceExists(int serviceId) {
        return !jdbcTemplate.queryForList("SELECT id FROM rf_service_rh WHERE id = :id", Map.of("id", serviceId)).isEmpty();
    }

    private boolean userExistsByIdGenerate(String idGenerate) {
        return !jdbcTemplate.queryForList(
                "SELECT id FROM sys_users WHERE id_generate = :id LIMIT 1", Map.of("id", idGenerate)).isEmpty();
    }

    private boolean affectationExists(int agentId) {
        return !jdbcTemplate.queryForList(
                "SELECT id FROM op_affectation_rh WHERE agent_id = :id", Map.of("id", agentId)).isEmpty();
    }

    private int requireInt(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value == null) {
            throw new BusinessException(400, key + " est requis.");
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private String requireString(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            throw new BusinessException(400, key + " est requis.");
        }
        return String.valueOf(value);
    }

    private Integer optionalInt(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? null : Integer.parseInt(String.valueOf(value));
    }
}
