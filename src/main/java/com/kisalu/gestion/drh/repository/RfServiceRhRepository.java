package com.kisalu.gestion.drh.repository;

import com.kisalu.gestion.drh.model.RfServiceRh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RfServiceRhRepository extends JpaRepository<RfServiceRh, Integer> {

    Optional<RfServiceRh> findByLibelle(String libelle);
}
