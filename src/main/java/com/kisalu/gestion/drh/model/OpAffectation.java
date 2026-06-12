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


import com.kisalu.gestion.drh.model.OpAgent;
import com.kisalu.gestion.drh.model.OpPdv;
import com.kisalu.gestion.drh.model.RfBus;
import com.kisalu.gestion.drh.model.RfSite;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_affectation")
public class OpAffectation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_agent")
    private OpAgent idAgent;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_bus", nullable = true)
    private RfBus idBus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private RfSite siteId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdv_id")
    private OpPdv pdvId;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;

    public OpAffectation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OpAgent getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(OpAgent idAgent) {
        this.idAgent = idAgent;
    }

    public RfBus getIdBus() {
        return idBus;
    }

    public void setIdBus(RfBus idBus) {
        this.idBus = idBus;
    }

    public RfSite getSiteId() {
        return siteId;
    }

    public void setSiteId(RfSite siteId) {
        this.siteId = siteId;
    }

    public OpPdv getPdvId() {
        return pdvId;
    }

    public void setPdvId(OpPdv pdvId) {
        this.pdvId = pdvId;
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
