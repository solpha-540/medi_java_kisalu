package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.repository.RfCommuneRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Creation d'adresse agent (equivalent PHP : AddressController::create). */
@Service
public class AddressService {

    private final RfCommuneRepository communeRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AddressService(RfCommuneRepository communeRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.communeRepository = communeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> createOrFindByCommune(Integer communeId, String createdByUserRf) {
        List<Map<String, Object>> existing = jdbcTemplate.queryForList(
                "SELECT Id AS id FROM op_adresse WHERE commune_id = :communeId LIMIT 1",
                Map.of("communeId", communeId));
        if (!existing.isEmpty()) {
            return existing.getFirst();
        }

        RfCommuneRepository.CommuneLocationRow location = communeRepository.findLocationByCommuneId(communeId)
                .orElseThrow(() -> new BusinessException(400, "cette commune n'est pas enregistrée chez nous"));

        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> params = new HashMap<>();
        params.put("provinceId", location.getProvinceId());
        params.put("communeId", location.getCommuneId());
        params.put("villeId", location.getVilleId());
        params.put("now", now);
        params.put("userRf", createdByUserRf);

        jdbcTemplate.update("""
                INSERT INTO op_adresse (province_id, commune_id, ville_id, last_update, created_at, created_by_user_id)
                VALUES (:provinceId, :communeId, :villeId, :now, :now, :userRf)
                """, params);

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        return Map.of("id", id);
    }
}
