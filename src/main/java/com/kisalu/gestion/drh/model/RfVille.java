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


import com.kisalu.gestion.drh.model.RfProvince;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "rf_ville")
public class RfVille {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(nullable = false)
    private String labele;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_province")
    private RfProvince idProvince;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;
    @Column(name = "CodeVille")
    private String codeville;

    public RfVille() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabele() {
        return labele;
    }

    public void setLabele(String labele) {
        this.labele = labele;
    }

    public RfProvince getIdProvince() {
        return idProvince;
    }

    public void setIdProvince(RfProvince idProvince) {
        this.idProvince = idProvince;
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

    public String getCodeville() {
        return codeville;
    }

    public void setCodeville(String codeville) {
        this.codeville = codeville;
    }
}
