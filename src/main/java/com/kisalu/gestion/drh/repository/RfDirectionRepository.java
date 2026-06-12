package com.kisalu.gestion.drh.repository;

import com.kisalu.gestion.drh.model.RfDirection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RfDirectionRepository extends JpaRepository<RfDirection, Integer> {

    Optional<RfDirection> findByLibele(String libele);
}
