package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Aptitude physique, cursus academique, adresses agents, Byroles. */
@Service
public class AgentCareerService {

    private static final String APTITUDE_JOIN = """
            SELECT a.*, ag.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom
            FROM rf_aptitude_physique a
            JOIN op_agent ag ON a.id_op_agent = ag.id
            JOIN rf_personnes p ON ag.personne_id = p.id
            """;

    private static final String CURSUS_JOIN = """
            SELECT c.*, ag.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom
            FROM rf_cursus_academique c
            JOIN op_agent ag ON c.id_op_agent = ag.id
            JOIN rf_personnes p ON ag.personne_id = p.id
            """;

    private static final String ADRESSE_SELECT = """
            SELECT ag.id AS agent_id, rf.id AS personne_id, rf.nom, rf.post_nom, rf.prenom,
                   rf.sexe, rf.mail, rf.photo, rf.avenue, rf.quartier, rf.num_parcelle,
                   con.telephone_un, com.labele AS commune, ville.labele AS ville, prov.labele AS province
            FROM op_agent ag
            LEFT JOIN rf_personnes rf ON rf.id = ag.personne_id
            LEFT JOIN rf_contact con ON con.id_op_agent = ag.id
            LEFT JOIN op_adresse adr ON adr.id = rf.adresse_id
            LEFT JOIN rf_commune com ON com.id = adr.commune_id
            LEFT JOIN rf_ville ville ON ville.id = adr.ville_id
            LEFT JOIN rf_province prov ON prov.id = adr.province_id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleAuthorizationService roleAuthorizationService;
    private final AutorisationService autorisationService;
    private final UpdateHistoryService updateHistoryService;

    public AgentCareerService(
            NamedParameterJdbcTemplate jdbcTemplate,
            RoleAuthorizationService roleAuthorizationService,
            AutorisationService autorisationService,
            UpdateHistoryService updateHistoryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleAuthorizationService = roleAuthorizationService;
        this.autorisationService = autorisationService;
        this.updateHistoryService = updateHistoryService;
    }

    @Transactional
    public Map<String, Object> registerAptitude(Map<String, String> body, AuthenticatedUser user) {
        requireCareerRole(user);
        for (String field : List.of("groupe_sanguin", "allerigie", "etat_de_sante", "id_op_agent")) {
            if (body.get(field) == null || body.get(field).isBlank()) {
                throw new BusinessException(400, "Le champ requis \"" + field + "\" est manquant.");
            }
        }

        int agentId = Integer.parseInt(body.get("id_op_agent"));
        jdbcTemplate.update("""
                INSERT INTO rf_aptitude_physique (groupe_sanguin, allerigie, etat_de_sante,
                    id_op_agent, id_user_created, created_at, last_updated)
                VALUES (:groupe, :allergie, :etat, :agentId, :userCreated, NOW(), NOW())
                """, Map.of(
                "groupe", body.get("groupe_sanguin").trim().toUpperCase(Locale.ROOT),
                "allergie", body.get("allerigie").trim(),
                "etat", body.get("etat_de_sante").trim(),
                "agentId", agentId,
                "userCreated", user.getUserRf()));

        jdbcTemplate.update("UPDATE op_agent SET statut = 1 WHERE id = :id", Map.of("id", agentId));

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        Map<String, Object> data = jdbcTemplate.queryForList("SELECT * FROM rf_aptitude_physique WHERE id = :id", Map.of("id", id)).getFirst();
        return Map.of("code", 200, "message", "Aptitude physique enregistrée avec succès.", "data", data);
    }

    public Map<String, Object> listAptitudes(Integer agentId) {
        if (agentId != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    APTITUDE_JOIN + " WHERE a.id_op_agent = :agentId", Map.of("agentId", agentId));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Aucune aptitude physique trouvée pour cet agent");
            }
            return Map.of("code", 200, "message", "Aptitudes physiques trouvées", "data", rows);
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(APTITUDE_JOIN, Map.of());
        return Map.of("code", 200, "message", "Aptitudes physiques récupérées avec succès.", "data", rows);
    }

    @Transactional
    public Map<String, Object> updateAptitude(Integer aptitudeId, Map<String, String> body, AuthenticatedUser user) {
        autorisationService.requireAutorisationToday(user.getUserRf());

        Map<String, Object> existing = findRow("rf_aptitude_physique", aptitudeId, "Aptitude physique non trouvée");
        Map<String, Object> updateData = extractUpdate(body, List.of("groupe_sanguin", "allerigie", "etat_de_sante", "id_op_agent"));
        if (updateData.isEmpty()) {
            throw new BusinessException(400, "Aucune donnée valide fournie");
        }
        if (updateData.containsKey("groupe_sanguin")) {
            updateData.put("groupe_sanguin", String.valueOf(updateData.get("groupe_sanguin")).trim().toUpperCase(Locale.ROOT));
        }

        applyPartialUpdate("rf_aptitude_physique", aptitudeId, updateData, "last_updated");
        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("id_op_agent")).intValue(),
                "rf_aptitude_physique", aptitudeId, user.getUserRf(), existing, updateData);

        Map<String, Object> updated = jdbcTemplate.queryForList(
                "SELECT * FROM rf_aptitude_physique WHERE id = :id", Map.of("id", aptitudeId)).getFirst();
        return Map.of("code", 200, "message", "Les informations ont été mises à jour", "data", updated);
    }

    @Transactional
    public Map<String, Object> registerCursus(Map<String, String> body, AuthenticatedUser user) {
        requireCareerRole(user);
        for (String field : List.of("niveau_etude", "filiere", "etablissement", "annee_terminale", "id_op_agent")) {
            if (body.get(field) == null || body.get(field).isBlank()) {
                throw new BusinessException(400, "Le champ requis \"" + field + "\" est vide ou manquant.");
            }
        }

        jdbcTemplate.update("""
                INSERT INTO rf_cursus_academique (niveau_etude, filiere, etablissement, annee_terminale,
                    id_op_agent, id_user_created, created_at, last_update)
                VALUES (:niveau, :filiere, :etablissement, :annee, :agentId, :userCreated, NOW(), NOW())
                """, Map.of(
                "niveau", body.get("niveau_etude").trim().toUpperCase(Locale.ROOT),
                "filiere", body.get("filiere").trim().toUpperCase(Locale.ROOT),
                "etablissement", body.get("etablissement").trim().toUpperCase(Locale.ROOT),
                "annee", body.get("annee_terminale").trim(),
                "agentId", Integer.parseInt(body.get("id_op_agent")),
                "userCreated", user.getUserRf()));

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        Map<String, Object> data = jdbcTemplate.queryForList(CURSUS_JOIN + " WHERE c.id = :id", Map.of("id", id)).getFirst();
        return Map.of("code", 200, "message", "Cursus académique enregistré avec succès", "data", data);
    }

    public Map<String, Object> listCursus(Integer agentId) {
        if (agentId != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    CURSUS_JOIN + " WHERE c.id_op_agent = :agentId", Map.of("agentId", agentId));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Aucun cursus académique trouvé pour cet agent");
            }
            return Map.of("code", 200, "message", "Cursus académique trouvé", "data", rows);
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(CURSUS_JOIN, Map.of());
        return Map.of("code", 200, "message", "Cursus académiques récupérés avec succès", "data", rows);
    }

    @Transactional
    public Map<String, Object> updateCursus(Integer cursusId, Map<String, String> body, AuthenticatedUser user) {
        autorisationService.requireAutorisationToday(user.getUserRf());

        Map<String, Object> existing = findRow("rf_cursus_academique", cursusId, "Cursus académique non trouvé");
        Map<String, Object> updateData = extractUpdate(body, List.of("niveau_etude", "filiere", "etablissement", "id_op_agent"));
        if (updateData.isEmpty()) {
            throw new BusinessException(400, "Aucune donnée valide fournie");
        }

        applyPartialUpdate("rf_cursus_academique", cursusId, updateData, "last_update");
        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("id_op_agent")).intValue(),
                "rf_cursus_academique", cursusId, user.getUserRf(), existing, updateData);

        Map<String, Object> updated = jdbcTemplate.queryForList(CURSUS_JOIN + " WHERE c.id = :id", Map.of("id", cursusId)).getFirst();
        return Map.of("code", 200, "message", "Les informations ont été mises à jour", "data", updated);
    }

    public Map<String, Object> getAgentsByRole() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT r.libelle AS role, u.id AS user_id, u.id_generate,
                       a.id AS agent_id, p.nom, p.post_nom, p.prenom
                FROM sys_roles r
                LEFT JOIN sys_user_roles ur ON ur.id_role = r.id
                LEFT JOIN sys_users u ON u.id = ur.id_users
                LEFT JOIN op_agent a ON a.code_generate_tac = u.id_generate
                LEFT JOIN rf_personnes p ON p.id = a.personne_id
                WHERE r.libelle IN ('chauffeur', 'verificateur', 'encadreur', 'convoyeur')
                ORDER BY r.libelle, p.nom
                """, Map.of());

        Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String role = String.valueOf(row.get("role"));
            if (row.get("nom") == null) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("agent_id", row.get("agent_id"));
            item.put("user_id", row.get("user_id"));
            item.put("id_generate", row.get("id_generate"));
            item.put("nom", row.get("nom"));
            item.put("post_nom", row.get("post_nom"));
            item.put("prenom", row.get("prenom"));
            grouped.computeIfAbsent(role, k -> new ArrayList<>()).add(item);
        }

        return Map.of("code", 200, "message", "Agents avec les rôles récupérés avec succès.", "data", grouped);
    }

    public Map<String, Object> historiqueAdresseAgent(Integer agentId, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef personnel");

        if (agentId != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    ADRESSE_SELECT + " WHERE ag.id = :agentId ORDER BY ag.created_at DESC",
                    Map.of("agentId", agentId));
            return Map.of("code", 200, "message", "Adresse de l'agent récupérées avec succès", "data", rows);
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                ADRESSE_SELECT + " ORDER BY ag.created_at DESC", Map.of());
        return Map.of("code", 200, "message", "Liste de toutes les adresses des agents récupérée avec succès", "data", rows);
    }

    private void requireCareerRole(AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef suivi carriere");
    }

    private Map<String, Object> findRow(String table, int id, String notFoundMessage) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM " + table + " WHERE id = :id", Map.of("id", id));
        if (rows.isEmpty()) {
            throw new BusinessException(404, notFoundMessage);
        }
        return rows.getFirst();
    }

    private Map<String, Object> extractUpdate(Map<String, String> body, List<String> allowedFields) {
        Map<String, Object> updateData = new HashMap<>();
        for (String field : allowedFields) {
            if (body.containsKey(field) && body.get(field) != null) {
                updateData.put(field, body.get(field));
            }
        }
        return updateData;
    }

    private void applyPartialUpdate(String table, int id, Map<String, Object> updateData, String timestampColumn) {
        StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ");
        Map<String, Object> params = new HashMap<>();
        int i = 0;
        for (Map.Entry<String, Object> entry : updateData.entrySet()) {
            if (i++ > 0) {
                sql.append(", ");
            }
            String param = "p" + i;
            sql.append(entry.getKey()).append(" = :").append(param);
            params.put(param, entry.getValue());
        }
        sql.append(", ").append(timestampColumn).append(" = NOW() WHERE id = :id");
        params.put("id", id);
        jdbcTemplate.update(sql.toString(), params);
    }
}
