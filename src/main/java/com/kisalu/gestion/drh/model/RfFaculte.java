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


import com.kisalu.gestion.drh.model.RfEtablissement;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "rf_faculte")
public class RfFaculte {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_etablissement")
    private RfEtablissement idEtablissement;
    @Column(name = "libele_faculte", nullable = false)
    private String libeleFaculte;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;

    public RfFaculte() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RfEtablissement getIdEtablissement() {
        return idEtablissement;
    }

    public void setIdEtablissement(RfEtablissement idEtablissement) {
        this.idEtablissement = idEtablissement;
    }

    public String getLibeleFaculte() {
        return libeleFaculte;
    }

    public void setLibeleFaculte(String libeleFaculte) {
        this.libeleFaculte = libeleFaculte;
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
}
