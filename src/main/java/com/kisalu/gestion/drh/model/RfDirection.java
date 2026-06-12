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


import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "rf_direction")
public class RfDirection {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(nullable = false)
    private String libele;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;
    @Column(name = "id_directeur")
    private Integer idDirecteur;
    @Column(name = "code_direction")
    private String codeDirection;

    public RfDirection() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibele() {
        return libele;
    }

    public void setLibele(String libele) {
        this.libele = libele;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SysUsers getIdUserCreatedAt() {
        return idUserCreatedAt;
    }

    public void setIdUserCreatedAt(SysUsers idUserCreatedAt) {
        this.idUserCreatedAt = idUserCreatedAt;
    }

    public Integer getIdDirecteur() {
        return idDirecteur;
    }

    public void setIdDirecteur(Integer idDirecteur) {
        this.idDirecteur = idDirecteur;
    }

    public String getCodeDirection() {
        return codeDirection;
    }

    public void setCodeDirection(String codeDirection) {
        this.codeDirection = codeDirection;
    }
}
