package com.kisalu.gestion.drh.repository;

import com.kisalu.gestion.drh.model.SysUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysUsersRepository extends JpaRepository<SysUsers, Integer> {

    Optional<SysUsers> findByLogin(String login);

    Optional<SysUsers> findByIdGenerate(String idGenerate);
}
