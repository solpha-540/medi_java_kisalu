package com.kisalu.gestion.drh.repository;

import com.kisalu.gestion.drh.model.RfGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RfGradeRepository extends JpaRepository<RfGrade, Integer> {

    Optional<RfGrade> findByLibelle(String libelle);
}
