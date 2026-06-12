package com.kisalu.gestion.drh.repository;

import com.kisalu.gestion.drh.model.SysUserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SysUserRolesRepository extends JpaRepository<SysUserRoles, Integer> {

    @Query("""
            SELECT r.libelle FROM SysUserRoles ur
            JOIN ur.idRole r
            WHERE ur.idUsers.id = :userId
            """)
    List<String> findRoleLabelsByUserId(@Param("userId") Integer userId);
}
