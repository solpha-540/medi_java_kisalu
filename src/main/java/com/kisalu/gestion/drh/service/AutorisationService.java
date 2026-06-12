package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Autorisations DRH (module autorisation PHP). */
@Service
public class AutorisationService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleAuthorizationService roleAuthorizationService;

    public AutorisationService(NamedParameterJdbcTemplate jdbcTemplate, RoleAuthorizationService roleAuthorizationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleAuthorizationService = roleAuthorizationService;
    }

    public Map<String, Object> list(String idOpAgent, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");

        if (idOpAgent != null && !idOpAgent.isBlank()) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM rf_autorisation WHERE id_op_agent = :agent", Map.of("agent", idOpAgent));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Aucune autorisation trouvée pour cet agent");
            }
            return Map.of("code", 200, "message", "Autorisation de l'agent récupérée avec succès", "data", rows);
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_autorisation", Map.of());
        if (rows.isEmpty()) {
            throw new BusinessException(404, "Aucune autorisation trouvée");
        }
        return Map.of("code", 200, "message", "Liste complète des autorisations", "data", rows);
    }

    @Transactional
    public Map<String, Object> create(Map<String, String> body, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");

        for (String field : List.of("id_op_agent", "statut", "id_demande")) {
            if (body.get(field) == null || body.get(field).isBlank()) {
                throw new BusinessException(400, "Le champ \"" + field + "\" est requis");
            }
        }

        String idOpAgent = body.get("id_op_agent");
        String statut = body.get("statut");
        Integer idDemande = Integer.parseInt(body.get("id_demande"));

        List<Map<String, Object>> existing = jdbcTemplate.queryForList("""
                SELECT * FROM rf_autorisation
                WHERE id_op_agent = :agent AND statut = 'valide' AND DATE(created_at) = :today
                ORDER BY created_at DESC LIMIT 1
                """, Map.of("agent", idOpAgent, "today", LocalDate.now()));

        if (!existing.isEmpty()) {
            Map<String, Object> conflict = new HashMap<>();
            conflict.put("code", 400);
            conflict.put("message", "une autorisation existe déjà pour cet agent aujourd'hui");
            conflict.put("data", existing.getFirst());
            return conflict;
        }

        jdbcTemplate.update("UPDATE rf_demande SET statut = :statut WHERE id = :id",
                Map.of("statut", statut, "id", idDemande));

        Map<String, Object> params = new HashMap<>();
        params.put("idOpAgent", idOpAgent);
        params.put("statut", statut);
        params.put("idDemande", idDemande);
        params.put("userCreated", user.getUserRf());

        jdbcTemplate.update("""
                INSERT INTO rf_autorisation (id_op_agent, statut, id_demande, id_user_created, created_at, updated_at)
                VALUES (:idOpAgent, :statut, :idDemande, :userCreated, NOW(), NOW())
                """, params);

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        Map<String, Object> data = jdbcTemplate.queryForList(
                "SELECT * FROM rf_autorisation WHERE id = :id", Map.of("id", id)).getFirst();

        String message = "valide".equals(statut) ? "Autorisation accordée avec succès" : "Autorisation refusée avec succès";
        return Map.of("code", 200, "message", message, "data", data);
    }

    /** RfAutorisationSerializer::checkAutorisation — autorisation valide du jour. */
    public void requireAutorisationToday(String idOpAgent) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT * FROM rf_autorisation
                WHERE id_op_agent = :agent AND statut = 'valide' AND DATE(created_at) = CURDATE()
                ORDER BY created_at DESC LIMIT 1
                """, Map.of("agent", idOpAgent));
        if (rows.isEmpty()) {
            throw new BusinessException(404, "Aucune autorisation active trouvée pour cet  agent aujourd'hui.");
        }
    }
}
