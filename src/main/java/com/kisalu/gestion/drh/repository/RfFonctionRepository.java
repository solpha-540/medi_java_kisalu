package com.kisalu.gestion.drh.repository;

import com.kisalu.gestion.drh.model.RfFonction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RfFonctionRepository extends JpaRepository<RfFonction, Integer> {

    Optional<RfFonction> findByLibelle(String libelle);
}
