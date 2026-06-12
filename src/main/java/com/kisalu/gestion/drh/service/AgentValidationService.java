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

/** Validations DRH (statut 3→4) et DG (statut 4→5). */
@Service
public class AgentValidationService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleAuthorizationService roleAuthorizationService;

    public AgentValidationService(NamedParameterJdbcTemplate jdbcTemplate, RoleAuthorizationService roleAuthorizationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleAuthorizationService = roleAuthorizationService;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> validationDrh(Map<String, Object> body, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "drh");
        return validateAgents(body, user, 3, 4, "La validation par le DRH a été effectué avec succès");
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> validationDg(Map<String, Object> body, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg");
        return validateAgents(body, user, 4, 5, "La validation par le DG a été effectué avec succès");
    }

    private Map<String, Object> validateAgents(Map<String, Object> body, AuthenticatedUser user,
                                               int requiredStatut, int targetStatut, String successMessage) {
        Object idsRaw = body.get("id_op_agent");
        if (!(idsRaw instanceof List<?> list)) {
            throw new BusinessException(400, "id_op_agent doit être un tableau");
        }

        List<Map<String, Object>> results = new ArrayList<>();
        for (Object rawId : list) {
            int agentId = Integer.parseInt(String.valueOf(rawId));
            Map<String, Object> item = new HashMap<>();
            item.put("agentId", agentId);
            item.put("status", "failed");

            Map<String, Object> agent = fetchAgent(agentId);
            if (agent == null) {
                item.put("detail", "Agent introuvable");
                results.add(item);
                continue;
            }

            int currentStatut = ((Number) agent.get("statut")).intValue();
            item.put("agent_statut", currentStatut);

            if (currentStatut == targetStatut) {
                item.put("detail", requiredStatut == 3 ? "Agent déjà validé" : "Agent déjà validé par la DG");
                results.add(item);
                continue;
            }
            if (currentStatut != requiredStatut) {
                item.put("detail", requiredStatut == 3
                        ? "Statut actuel ne permet pas la validation"
                        : "Statut actuel ne permet pas la validation DG");
                results.add(item);
                continue;
            }

            int updated = jdbcTemplate.update(
                    "UPDATE op_agent SET statut = :statut, last_update = NOW() WHERE id = :id AND statut = :required",
                    Map.of("statut", targetStatut, "id", agentId, "required", requiredStatut));
            if (updated == 0) {
                item.put("message", "Échec mise à jour statut");
                results.add(item);
                continue;
            }

            Map<String, Object> success = new HashMap<>();
            success.put("agentId", agentId);
            success.put("agent_matricule", agent.get("matricule"));
            success.put("agent_statut", targetStatut);
            success.put("nomCompletAgent", agent.get("nomCompletAgent"));
            success.put("statut", "success");
            success.put("detail", "Agent validé avec succès");
            results.add(success);
        }

        return Map.of("code", 200, "message", successMessage, "data", results);
    }

    private Map<String, Object> fetchAgent(int agentId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT a.id AS agentId, a.matricule, a.statut,
                       CONCAT(p.nom, ' ', p.post_nom, ' ', p.prenom) AS nomCompletAgent
                FROM op_agent a
                JOIN rf_personnes p ON a.personne_id = p.id
                WHERE a.id = :id
                """, Map.of("id", agentId));
        return rows.isEmpty() ? null : rows.getFirst();
    }
}
