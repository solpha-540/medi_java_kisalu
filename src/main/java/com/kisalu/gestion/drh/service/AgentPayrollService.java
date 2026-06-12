package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** Salaire, remuneration, securite sociale (module agent PHP). */
@Service
public class AgentPayrollService {

    private static final Pattern SECU_NUMERO = Pattern.compile("^[A-Za-z0-9\\-]+$");
    private static final String SALAIRE_JOIN = """
            SELECT s.*, a.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom
            FROM rf_salaire s
            JOIN op_agent a ON s.id_op_agent = a.id
            JOIN rf_personnes p ON a.personne_id = p.id
            """;

    private static final String REMUNERATION_JOIN = """
            SELECT r.*, a.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom,
                   pr.libele, pr.montant, pr.type_prime
            FROM rf_remuneration r
            JOIN op_agent a ON r.id_op_agent = a.id
            JOIN rf_personnes p ON a.personne_id = p.id
            JOIN rf_prime pr ON r.id_prime = pr.id
            """;

    private static final String SECU_JOIN = """
            SELECT s.*, a.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom
            FROM rf_securite_social s
            JOIN op_agent a ON s.id_op_agent = a.id
            JOIN rf_personnes p ON a.personne_id = p.id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleAuthorizationService roleAuthorizationService;
    private final AutorisationService autorisationService;
    private final UpdateHistoryService updateHistoryService;

    public AgentPayrollService(
            NamedParameterJdbcTemplate jdbcTemplate,
            RoleAuthorizationService roleAuthorizationService,
            AutorisationService autorisationService,
            UpdateHistoryService updateHistoryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleAuthorizationService = roleAuthorizationService;
        this.autorisationService = autorisationService;
        this.updateHistoryService = updateHistoryService;
    }

    private void requirePayrollRole(AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef bureau paie");
    }

    @Transactional
    public Map<String, Object> registerSalaire(Map<String, String> body, AuthenticatedUser user) {
        requirePayrollRole(user);
        for (String field : List.of("numero_compte", "nom_banque", "id_op_agent")) {
            if (body.get(field) == null || body.get(field).isBlank()) {
                throw new BusinessException(400, "Le champ requis \"" + field + "\" est manquant ou vide.");
            }
        }

        Map<String, Object> params = Map.of(
                "numeroCompte", body.get("numero_compte").trim(),
                "nomBanque", body.get("nom_banque").trim(),
                "agentId", Integer.parseInt(body.get("id_op_agent")),
                "userCreated", user.getUserRf());

        jdbcTemplate.update("""
                INSERT INTO rf_salaire (numero_compte, nom_banque, id_op_agent, id_user_created, created_at, last_update)
                VALUES (:numeroCompte, :nomBanque, :agentId, :userCreated, NOW(), NOW())
                """, params);

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        Map<String, Object> data = jdbcTemplate.queryForList(SALAIRE_JOIN + " WHERE s.id = :id", Map.of("id", id)).getFirst();
        return Map.of("code", 200, "message", "Salaire enregistré avec succès.", "data", data);
    }

    public Map<String, Object> listSalaires(Integer agentId) {
        if (agentId != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    SALAIRE_JOIN + " WHERE s.id_op_agent = :agentId", Map.of("agentId", agentId));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Aucun salaire trouvé pour cet agent");
            }
            return Map.of("code", 200, "message", "Salaire trouvé", "data", rows);
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(SALAIRE_JOIN, Map.of());
        return Map.of("code", 200, "message", "Salaires récupérés avec succès", "data", rows);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> registerRemuneration(Map<String, Object> body, AuthenticatedUser user) {
        requirePayrollRole(user);

        Object agentRaw = body.get("id_op_agent");
        Object primesRaw = body.get("primes");
        if (agentRaw == null || primesRaw == null) {
            throw new BusinessException(400, "Champ requis manquant : id_op_agent ou primes");
        }

        int agentId = Integer.parseInt(String.valueOf(agentRaw));
        List<Map<String, Object>> primes;
        if (primesRaw instanceof List<?> list) {
            primes = (List<Map<String, Object>>) list;
        } else {
            throw new BusinessException(400, "La liste des primes doit contenir au moins un élément.");
        }
        if (primes.isEmpty()) {
            throw new BusinessException(400, "La liste des primes doit contenir au moins un élément.");
        }

        List<Map<String, Object>> resultats = new ArrayList<>();
        for (Map<String, Object> prime : primes) {
            Object idPrime = prime.get("id_prime");
            if (idPrime == null) {
                continue;
            }
            Map<String, Object> params = Map.of(
                    "agentId", agentId,
                    "primeId", Integer.parseInt(String.valueOf(idPrime)),
                    "userCreated", user.getUserRf());

            jdbcTemplate.update("""
                    INSERT INTO rf_remuneration (id_op_agent, id_prime, id_user_created, created_at, last_update)
                    VALUES (:agentId, :primeId, :userCreated, NOW(), NOW())
                    """, params);

            Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
            resultats.add(jdbcTemplate.queryForList("""
                    SELECT r.*, a.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom,
                           pr.libele, pr.montant, pr.type_prime
                    FROM rf_remuneration r
                    JOIN op_agent a ON r.id_op_agent = a.id
                    JOIN rf_personnes p ON a.personne_id = p.id
                    JOIN rf_prime pr ON r.id_prime = pr.id
                    WHERE r.id = :id
                    """, Map.of("id", id)).getFirst());
        }

        jdbcTemplate.update("UPDATE op_agent SET statut = 3 WHERE id = :id", Map.of("id", agentId));

        return Map.of("code", 200, "message", "Rémunérations enregistrées avec succès.", "data", resultats);
    }

    public Map<String, Object> listRemunerations(Integer agentId) {
        String sql = """
                SELECT r.*, a.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom,
                       pr.libele, pr.montant, pr.type_prime
                FROM rf_remuneration r
                JOIN op_agent a ON r.id_op_agent = a.id
                JOIN rf_personnes p ON a.personne_id = p.id
                JOIN rf_prime pr ON r.id_prime = pr.id
                """;
        if (agentId != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql + " WHERE r.id_op_agent = :agentId", Map.of("agentId", agentId));
            return Map.of("code", 200, "message", "Rémunérations récupérées", "data", rows);
        }
        return Map.of("code", 200, "message", "Rémunérations récupérées", "data", jdbcTemplate.queryForList(sql, Map.of()));
    }

    @Transactional
    public Map<String, Object> registerSecuriteSocial(Map<String, String> body, AuthenticatedUser user) {
        requirePayrollRole(user);
        for (String field : List.of("numero", "id_op_agent")) {
            if (body.get(field) == null || body.get(field).isBlank()) {
                throw new BusinessException(400, "Le champ requis \"" + field + "\" est vide ou manquant.");
            }
        }
        String numero = body.get("numero").trim();
        if (!SECU_NUMERO.matcher(numero).matches()) {
            throw new BusinessException(400, "Le numéro de sécurité sociale est invalide.");
        }

        Map<String, Object> params = Map.of(
                "numero", numero,
                "agentId", Integer.parseInt(body.get("id_op_agent")),
                "userCreated", user.getUserRf());

        jdbcTemplate.update("""
                INSERT INTO rf_securite_social (numero, id_op_agent, id_user_created, created_at, last_update)
                VALUES (:numero, :agentId, :userCreated, NOW(), NOW())
                """, params);

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        Map<String, Object> data = jdbcTemplate.queryForList("""
                SELECT s.*, a.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom
                FROM rf_securite_social s
                JOIN op_agent a ON s.id_op_agent = a.id
                JOIN rf_personnes p ON a.personne_id = p.id
                WHERE s.id = :id
                """, Map.of("id", id)).getFirst();

        return Map.of("code", 200, "message", "Sécurité sociale enregistrée avec succès.", "data", data);
    }

    public Map<String, Object> listSecuriteSocial(Integer agentId) {
        String sql = """
                SELECT s.*, a.matricule AS agent_matricule, p.nom AS agent_nom, p.prenom AS agent_prenom
                FROM rf_securite_social s
                JOIN op_agent a ON s.id_op_agent = a.id
                JOIN rf_personnes p ON a.personne_id = p.id
                """;
        if (agentId != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql + " WHERE s.id_op_agent = :agentId", Map.of("agentId", agentId));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Aucune sécurité sociale trouvée");
            }
            return Map.of("code", 200, "message", "Sécurité sociale trouvée", "data", rows);
        }
        return Map.of("code", 200, "message", "Sécurités sociales récupérées avec succès.", "data", jdbcTemplate.queryForList(sql, Map.of()));
    }

    @Transactional
    public Map<String, Object> updateSalaire(Integer salaireId, Map<String, String> body, AuthenticatedUser user) {
        autorisationService.requireAutorisationToday(user.getUserRf());

        Map<String, Object> existing = findRowOrThrow("rf_salaire", salaireId, "Salaire non trouvé");
        Map<String, Object> updateData = extractUpdate(body, List.of("numero_compte", "nom_banque", "id_op_agent"));
        if (updateData.isEmpty()) {
            throw new BusinessException(400, "Aucune donnée valide fournie");
        }

        applyPartialUpdate("rf_salaire", salaireId, updateData);
        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("id_op_agent")).intValue(),
                "rf_salaire", salaireId, user.getUserRf(), existing, updateData);

        Map<String, Object> data = jdbcTemplate.queryForList(SALAIRE_JOIN + " WHERE s.id = :id", Map.of("id", salaireId)).getFirst();
        return Map.of("code", 200, "message", "Les informations ont été mises à jour", "data", data);
    }

    @Transactional
    public Map<String, Object> updateRemuneration(Integer remunerationId, Map<String, String> body, AuthenticatedUser user) {
        autorisationService.requireAutorisationToday(user.getUserRf());

        Map<String, Object> existing = findRowOrThrow("rf_remuneration", remunerationId, "Rémunération non trouvée");
        Map<String, Object> updateData = extractUpdate(body, List.of("id_prime", "id_op_agent"));
        if (updateData.isEmpty()) {
            throw new BusinessException(400, "Aucune donnée valide fournie");
        }

        applyPartialUpdate("rf_remuneration", remunerationId, updateData);
        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("id_op_agent")).intValue(),
                "rf_remuneration", remunerationId, user.getUserRf(), existing, updateData);

        Map<String, Object> data = jdbcTemplate.queryForList(REMUNERATION_JOIN + " WHERE r.id = :id", Map.of("id", remunerationId)).getFirst();
        return Map.of("code", 200, "message", "Les informations ont été mises à jour", "data", data);
    }

    @Transactional
    public Map<String, Object> updateSecuriteSocial(Integer securiteId, Map<String, String> body, AuthenticatedUser user) {
        autorisationService.requireAutorisationToday(user.getUserRf());

        Map<String, Object> existing = findRowOrThrow("rf_securite_social", securiteId, "Securité sociale non trouvée");
        Map<String, Object> updateData = extractUpdate(body, List.of("numero", "id_op_agent"));
        if (updateData.isEmpty()) {
            throw new BusinessException(400, "Aucune donnée valide fournie");
        }
        if (updateData.containsKey("numero")) {
            String numero = String.valueOf(updateData.get("numero")).trim();
            if (!SECU_NUMERO.matcher(numero).matches()) {
                throw new BusinessException(400, "Le numéro de sécurité sociale est invalide.");
            }
            updateData.put("numero", numero);
        }

        applyPartialUpdate("rf_securite_social", securiteId, updateData);
        updateHistoryService.fillUpdateHistory(
                ((Number) existing.get("id_op_agent")).intValue(),
                "rf_securite_social", securiteId, user.getUserRf(), existing, updateData);

        Map<String, Object> data = jdbcTemplate.queryForList(SECU_JOIN + " WHERE s.id = :id", Map.of("id", securiteId)).getFirst();
        return Map.of("code", 200, "message", "Les informations ont été mises à jour", "data", data);
    }

    @Transactional
    public Map<String, Object> deleteSecuriteSocial(Integer id) {
        if (id == null) {
            throw new BusinessException(400, "ID requis pour la suppression.");
        }
        int deleted = jdbcTemplate.update("DELETE FROM rf_securite_social WHERE id = :id", Map.of("id", id));
        if (deleted == 0) {
            throw new BusinessException(404, "Aucune sécurité sociale trouvée avec cet ID");
        }
        return Map.of("code", 200, "message", "Sécurité sociale supprimée avec succès");
    }

    private Map<String, Object> findRowOrThrow(String table, int id, String notFoundMessage) {
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

    private void applyPartialUpdate(String table, int id, Map<String, Object> updateData) {
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
        sql.append(", last_update = NOW() WHERE id = :id");
        params.put("id", id);
        jdbcTemplate.update(sql.toString(), params);
    }
}
