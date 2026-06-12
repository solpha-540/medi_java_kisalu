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


import com.kisalu.gestion.drh.model.RfBus;
import com.kisalu.gestion.drh.model.RfServiceRh;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_planning_park")
public class OpPlanningPark {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service")
    private RfServiceRh idService;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bus")
    private RfBus idBus;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created", referencedColumnName = "id_generate")
    private SysUsers idUserCreated;

    public OpPlanningPark() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RfServiceRh getIdService() {
        return idService;
    }

    public void setIdService(RfServiceRh idService) {
        this.idService = idService;
    }

    public RfBus getIdBus() {
        return idBus;
    }

    public void setIdBus(RfBus idBus) {
        this.idBus = idBus;
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
