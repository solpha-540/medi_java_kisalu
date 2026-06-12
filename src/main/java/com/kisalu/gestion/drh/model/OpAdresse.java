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


import com.kisalu.gestion.drh.model.RfCommune;
import com.kisalu.gestion.drh.model.RfProvince;
import com.kisalu.gestion.drh.model.RfVille;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_adresse")
public class OpAdresse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = true)
    private RfProvince provinceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commune_id", nullable = true)
    private RfCommune communeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ville_id", nullable = true)
    private RfVille villeId;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id_generate")
    private SysUsers createdByUserId;

    public OpAdresse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RfProvince getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(RfProvince provinceId) {
        this.provinceId = provinceId;
    }

    public RfCommune getCommuneId() {
        return communeId;
    }

    public void setCommuneId(RfCommune communeId) {
        this.communeId = communeId;
    }

    public RfVille getVilleId() {
        return villeId;
    }

    public void setVilleId(RfVille villeId) {
        this.villeId = villeId;
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

    public SysUsers getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(SysUsers createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
