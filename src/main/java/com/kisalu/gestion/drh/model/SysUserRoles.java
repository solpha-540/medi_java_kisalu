package com.kisalu.gestion.drh.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import com.kisalu.gestion.drh.model.SysRoles;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "sys_user_roles")
public class SysUserRoles {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users")
    private SysUsers idUsers;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role")
    private SysRoles idRole;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created", referencedColumnName = "id_generate")
    private SysUsers idUserCreated;

    public SysUserRoles() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SysUsers getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(SysUsers idUsers) {
        this.idUsers = idUsers;
    }

    public SysRoles getIdRole() {
        return idRole;
    }

    public void setIdRole(SysRoles idRole) {
        this.idRole = idRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public SysUsers getIdUserCreated() {
        return idUserCreated;
    }

    public void setIdUserCreated(SysUsers idUserCreated) {
        this.idUserCreated = idUserCreated;
    }
}
