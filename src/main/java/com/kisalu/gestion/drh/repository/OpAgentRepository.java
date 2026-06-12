package com.kisalu.gestion.drh.repository;

import com.kisalu.gestion.drh.model.OpAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpAgentRepository extends JpaRepository<OpAgent, Integer> {

    @Query(value = """
            SELECT COUNT(*) > 0 FROM op_agent a
            JOIN rf_personnes p ON a.personne_id = p.id
            WHERE p.nom = :nom AND p.post_nom = :postNom AND p.prenom = :prenom
            """, nativeQuery = true)
    boolean existsByIdentity(@Param("nom") String nom, @Param("postNom") String postNom, @Param("prenom") String prenom);
}
