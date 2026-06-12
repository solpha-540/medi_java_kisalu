package com.kisalu.gestion.drh.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/** Historique des modifications (RfHistoriqueUpdateSerializer PHP). */
@Service
public class UpdateHistoryService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public UpdateHistoryService(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void fillUpdateHistory(
            int idOpAgentModifie,
            String tableModifie,
            int idLigneModifie,
            String idUserCreated,
            Map<String, Object> existingRow,
            Map<String, Object> updateData) {

        Map<String, Object> donnees = extractDonneesAvant(existingRow, updateData);
        Map<String, Object> params = new HashMap<>();
        params.put("agentModifie", idOpAgentModifie);
        params.put("table", tableModifie);
        params.put("ligneId", idLigneModifie);
        params.put("userCreated", idUserCreated);
        params.put("donnees", toJson(donnees));

        jdbcTemplate.update("""
                INSERT INTO rf_historique_update (id_op_agent_modifie, table_modifie, id_ligne_modifie,
                    id_user_created, donnees, created_at, updated_at)
                VALUES (:agentModifie, :table, :ligneId, :userCreated, :donnees, NOW(), NOW())
                """, params);
    }

    private Map<String, Object> extractDonneesAvant(Map<String, Object> existing, Map<String, Object> updateData) {
        Map<String, Object> before = new HashMap<>();
        for (String column : updateData.keySet()) {
            before.put(column, existing.get(column));
        }
        return before;
    }

    private String toJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}
