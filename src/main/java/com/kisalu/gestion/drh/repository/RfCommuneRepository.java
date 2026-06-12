package com.kisalu.gestion.drh.repository;

import com.kisalu.gestion.drh.model.RfCommune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RfCommuneRepository extends JpaRepository<RfCommune, Integer> {

    @Query(value = """
            SELECT c.Id AS communeId, v.Id AS villeId, p.Id AS provinceId
            FROM rf_commune c
            JOIN rf_ville v ON c.Id_ville = v.Id
            JOIN rf_province p ON v.Id_province = p.Id
            WHERE c.Id = :communeId
            """, nativeQuery = true)
    Optional<CommuneLocationRow> findLocationByCommuneId(@Param("communeId") Integer communeId);

    interface CommuneLocationRow {
        Integer getCommuneId();

        Integer getVilleId();

        Integer getProvinceId();
    }
}
