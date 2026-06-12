package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Formations RH collectives (module formation PHP - rf_formationRh). */
@Service
public class FormationRhService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FormationRhService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body, AuthenticatedUser user) {
        List<String> required = List.of("agent_id", "nom_centre", "nombre_personnes", "date_debut", "date_fin", "montant");
        List<String> missed = new ArrayList<>();
        for (String field : required) {
            Object val = body.get(field);
            if (val == null || String.valueOf(val).isBlank()) {
                missed.add(field);
            }
        }
        if (!missed.isEmpty()) {
            throw new BusinessException(400, missed.size() == 1
                    ? "Le champ " + missed.getFirst() + " est obligatoire"
                    : "Les champs " + String.join(", ", missed) + " sont obligatoires");
        }

        List<Integer> agentIds = parseAgentIds(body.get("agent_id"));
        LocalDate dateDebut = parseDate(body.get("date_debut"));
        LocalDate dateFin = parseDate(body.get("date_fin"));
        if (dateFin.isBefore(dateDebut)) {
            throw new BusinessException(400, "La date de fin doit être après la date de début");
        }

        BigDecimal montant = new BigDecimal(String.valueOf(body.get("montant")));
        if (montant.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(400, "Le montant doit être un nombre positif");
        }
        int nombrePersonnes = Integer.parseInt(String.valueOf(body.get("nombre_personnes")));
        if (nombrePersonnes < 1) {
            throw new BusinessException(400, "Le nombre de personnes doit être au minimum 1");
        }

        List<Integer> invalidAgents = new ArrayList<>();
        for (Integer agentId : agentIds) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id FROM op_agent WHERE id = :id", Map.of("id", agentId));
            if (rows.isEmpty()) {
                invalidAgents.add(agentId);
            }
        }
        if (!invalidAgents.isEmpty()) {
            throw new BusinessException(404, "Un ou plusieurs agents n'existent pas");
        }

        List<Map<String, Object>> created = new ArrayList<>();
        for (Integer agentId : agentIds) {
            Map<String, Object> params = new HashMap<>();
            params.put("agentId", agentId);
            params.put("nomCentre", String.valueOf(body.get("nom_centre")));
            params.put("nombrePersonnes", nombrePersonnes);
            params.put("dateDebut", dateDebut);
            params.put("dateFin", dateFin);
            params.put("montant", montant);
            params.put("description", body.get("description"));
            params.put("statut", body.getOrDefault("statut", "en_attente"));
            params.put("userCreated", user.getUserRf());

            jdbcTemplate.update("""
                    INSERT INTO rf_formationRh (agent_id, nom_centre, nombre_personnes, date_debut, date_fin,
                        montant, description, statut, id_user_created, created_at, last_update)
                    VALUES (:agentId, :nomCentre, :nombrePersonnes, :dateDebut, :dateFin,
                        :montant, :description, :statut, :userCreated, NOW(), NOW())
                    """, params);

            Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
            created.add(jdbcTemplate.queryForList("SELECT * FROM rf_formationRh WHERE id = :id", Map.of("id", id)).getFirst());
        }

        return Map.of(
                "code", 201,
                "message", "Formation(s) enregistrée(s) avec succès",
                "data", Map.of("total", created.size(), "formations", created));
    }

    public Map<String, Object> listAll() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_formationRh", Map.of());
        return Map.of("code", 200, "message", "Formations récupérées avec succès", "data", rows);
    }

    public Map<String, Object> getById(Integer id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM rf_formationRh WHERE id = :id", Map.of("id", id));
        if (rows.isEmpty()) {
            throw new BusinessException(404, "Formation non trouvée");
        }
        return Map.of("code", 200, "message", "Formation récupérée avec succès", "data", rows.getFirst());
    }

    public Map<String, Object> getByAgent(Integer agentId) {
        if (agentId == null) {
            throw new BusinessException(400, "L'ID de l'agent est requis");
        }
        List<Map<String, Object>> agent = jdbcTemplate.queryForList(
                "SELECT id FROM op_agent WHERE id = :id", Map.of("id", agentId));
        if (agent.isEmpty()) {
            throw new BusinessException(404, "L'agent spécifié n'existe pas");
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM rf_formationRh WHERE agent_id = :agentId", Map.of("agentId", agentId));
        return Map.of("code", 200, "message", "Formations de l'agent récupérées avec succès", "data", rows);
    }

    @Transactional
    public Map<String, Object> update(Integer id, Map<String, Object> body) {
        List<Map<String, Object>> existing = jdbcTemplate.queryForList(
                "SELECT * FROM rf_formationRh WHERE id = :id", Map.of("id", id));
        if (existing.isEmpty()) {
            throw new BusinessException(404, "Formation non trouvée");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        List<String> sets = new ArrayList<>();

        if (body.containsKey("nom_centre")) {
            sets.add("nom_centre = :nomCentre");
            params.put("nomCentre", body.get("nom_centre"));
        }
        if (body.containsKey("nombre_personnes")) {
            sets.add("nombre_personnes = :nombrePersonnes");
            params.put("nombrePersonnes", body.get("nombre_personnes"));
        }
        if (body.containsKey("date_debut")) {
            sets.add("date_debut = :dateDebut");
            params.put("dateDebut", parseDate(body.get("date_debut")));
        }
        if (body.containsKey("date_fin")) {
            sets.add("date_fin = :dateFin");
            params.put("dateFin", parseDate(body.get("date_fin")));
        }
        if (body.containsKey("montant")) {
            sets.add("montant = :montant");
            params.put("montant", new BigDecimal(String.valueOf(body.get("montant"))));
        }
        if (body.containsKey("description")) {
            sets.add("description = :description");
            params.put("description", body.get("description"));
        }
        if (body.containsKey("statut")) {
            sets.add("statut = :statut");
            params.put("statut", body.get("statut"));
        }
        if (sets.isEmpty()) {
            throw new BusinessException(400, "Aucun champ à mettre à jour");
        }
        sets.add("last_update = NOW()");
        jdbcTemplate.update("UPDATE rf_formationRh SET " + String.join(", ", sets) + " WHERE id = :id", params);

        return Map.of("code", 200, "message", "Formation mise à jour avec succès",
                "data", jdbcTemplate.queryForList("SELECT * FROM rf_formationRh WHERE id = :id", Map.of("id", id)).getFirst());
    }

    private List<Integer> parseAgentIds(Object raw) {
        List<Integer> ids = new ArrayList<>();
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                ids.add(Integer.parseInt(String.valueOf(item)));
            }
        } else {
            ids.add(Integer.parseInt(String.valueOf(raw)));
        }
        return ids;
    }

    private LocalDate parseDate(Object raw) {
        try {
            return LocalDate.parse(String.valueOf(raw));
        } catch (DateTimeParseException ex) {
            throw new BusinessException(400, "Format de date invalide. Utilisez le format YYYY-MM-DD");
        }
    }
}
