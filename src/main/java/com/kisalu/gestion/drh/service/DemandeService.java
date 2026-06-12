package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Demandes RH (module demande PHP — table rf_demande). */
@Service
public class DemandeService {

    private static final String SELECT_DEMANDES = """
            SELECT
                rd.*,
                op_dem.id AS id_op_agent_demandeur,
                op_dem.code_generate_tac AS code_generate_tac_demandeur,
                rf_dem.nom AS nom_demandeur,
                rf_dem.post_nom AS post_nom_demandeur,
                rf_dem.prenom AS prenom_demandeur,
                srv.libelle AS service_demandeur,
                op_rec.id AS id_op_agent_recepteur,
                op_rec.code_generate_tac AS code_generate_tac_recepteur,
                rf_rec.nom AS nom_recepteur,
                rf_rec.post_nom AS post_nom_recepteur,
                rf_rec.prenom AS prenom_recepteur
            FROM rf_demande AS rd
            JOIN op_agent AS op_dem ON op_dem.id = rd.id_agent_demandeur
            JOIN rf_personnes AS rf_dem ON rf_dem.id = op_dem.personne_id
            LEFT JOIN op_affectation_rh AS aff_rh ON aff_rh.agent_id = op_dem.id
            LEFT JOIN rf_service_rh AS srv ON srv.id = aff_rh.service_id
            JOIN op_agent AS op_rec ON op_rec.id = rd.id_agent_recepteur
            JOIN rf_personnes AS rf_rec ON rf_rec.id = op_rec.personne_id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DemandeService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> listAll() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                SELECT_DEMANDES + " ORDER BY rd.created_at DESC", Map.of());
        return listResponse(rows, "Données récupérées avec succès.", "Aucune demande trouvée.");
    }

    public Map<String, Object> listByRecepteur(String idRecepteur) {
        if (idRecepteur == null || idRecepteur.isBlank()) {
            return Map.of("code", 400, "message", "L'identifiant du récepteur est requis.", "data", List.of());
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                SELECT_DEMANDES + " WHERE rd.id_agent_recepteur = :idRecepteur ORDER BY rd.created_at DESC",
                Map.of("idRecepteur", idRecepteur));
        return listResponse(rows, "Demandes récupérées avec succès.", "Aucune demande trouvée pour ce récepteur.");
    }

    public Map<String, Object> listByDemandeur(String idDemandeur) {
        if (idDemandeur == null || idDemandeur.isBlank()) {
            return Map.of("code", 400, "message", "L'identifiant du demandeur est requis.", "data", List.of());
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                SELECT_DEMANDES + " WHERE rd.id_agent_demandeur = :idDemandeur ORDER BY rd.created_at DESC",
                Map.of("idDemandeur", idDemandeur));
        return listResponse(rows, "Demandes récupérées avec succès.", "Aucune demande trouvée pour ce demandeur.");
    }

    @Transactional
    public Map<String, Object> create(Map<String, String> body, AuthenticatedUser user) {
        List<String> missed = new ArrayList<>();
        for (String key : List.of("type_demande", "message", "id_agent_recepteur")) {
            if (body.get(key) == null || body.get(key).isBlank()) {
                missed.add(key);
            }
        }
        if (!missed.isEmpty()) {
            throw new BusinessException(400, "Les clés suivantes sont manquantes : " + String.join(", ", missed));
        }

        String message = body.get("message");
        if (!validatePlainText(message)) {
            throw new BusinessException(400, "Le message ne doit pas contenir de balises HTML ni de caractères spéciaux");
        }

        Integer idAgentDemandeur = findAgentIdByUserRf(user.getUserRf());
        Integer idAgentRecepteur = parseAgentId(body.get("id_agent_recepteur"));
        ensureAgentExists(idAgentRecepteur, "Le récepteur spécifié n'existe pas");

        Map<String, Object> params = new HashMap<>();
        params.put("typeDemande", body.get("type_demande").trim());
        params.put("message", message.trim());
        params.put("idAgentDemandeur", idAgentDemandeur);
        params.put("idAgentRecepteur", idAgentRecepteur);

        jdbcTemplate.update("""
                INSERT INTO rf_demande (type_demande, message, id_agent_demandeur, id_agent_recepteur,
                    statut, created_at, updated_at)
                VALUES (:typeDemande, :message, :idAgentDemandeur, :idAgentRecepteur, 'en_attente', NOW(), NOW())
                """, params);

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM rf_demande WHERE id = :id", Map.of("id", id));
        return Map.of("code", 201, "message", "Demande créée avec succès", "data", rows.getFirst());
    }

    private Map<String, Object> listResponse(List<Map<String, Object>> rows, String successMessage, String emptyMessage) {
        if (rows.isEmpty()) {
            return Map.of("code", 200, "message", emptyMessage, "data", List.of());
        }
        List<Map<String, Object>> data = rows.stream().map(this::toDemandeView).toList();
        return Map.of("code", 200, "message", successMessage, "data", data);
    }

    private Map<String, Object> toDemandeView(Map<String, Object> row) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", row.get("id"));
        item.put("type_demande", row.get("type_demande"));
        item.put("message", row.get("message"));
        item.put("date_creation", row.get("created_at"));
        item.put("statut_demande", row.get("statut") != null ? row.get("statut") : "en_attente");

        Map<String, Object> demandeur = new LinkedHashMap<>();
        demandeur.put("id_op_agent", row.get("id_op_agent_demandeur"));
        demandeur.put("code_generate_tac", row.get("code_generate_tac_demandeur"));
        demandeur.put("nom", row.get("nom_demandeur"));
        demandeur.put("post_nom", row.get("post_nom_demandeur"));
        demandeur.put("prenom", row.get("prenom_demandeur"));
        demandeur.put("service", row.get("service_demandeur"));
        item.put("identite_demandeur", demandeur);

        Map<String, Object> recepteur = new LinkedHashMap<>();
        recepteur.put("id_op_agent", row.get("id_op_agent_recepteur"));
        recepteur.put("code_generate_tac", row.get("code_generate_tac_recepteur"));
        recepteur.put("nom", row.get("nom_recepteur"));
        recepteur.put("post_nom", row.get("post_nom_recepteur"));
        recepteur.put("prenom", row.get("prenom_recepteur"));
        item.put("identite_recepteur", recepteur);

        return item;
    }

    private Integer findAgentIdByUserRf(String userRf) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id FROM op_agent WHERE code_generate_tac = :userRf LIMIT 1",
                Map.of("userRf", userRf));
        if (rows.isEmpty()) {
            throw new BusinessException(404, "Agent demandeur introuvable pour l'utilisateur connecté");
        }
        return ((Number) rows.getFirst().get("id")).intValue();
    }

    private Integer parseAgentId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw new BusinessException(400, "Identifiant agent invalide");
        }
    }

    private void ensureAgentExists(Integer agentId, String message) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id FROM op_agent WHERE id = :id LIMIT 1", Map.of("id", agentId));
        if (rows.isEmpty()) {
            throw new BusinessException(404, message);
        }
    }

    /** Equivalent PHP RfDemandeSerializer::validatePlainText */
    private boolean validatePlainText(String text) {
        String decoded = HtmlUtils.htmlUnescape(text);
        String stripped = decoded.replaceAll("<[^>]+>", "");
        return decoded.equals(stripped);
    }
}
