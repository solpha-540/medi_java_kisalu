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


import com.kisalu.gestion.drh.model.OpSignalementGenerale;
import com.kisalu.gestion.drh.model.RfLogBus;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "rf_suivis_mecanique")
public class RfSuivisMecanique {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_signalement")
    private OpSignalementGenerale idSignalement;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_log_bus")
    private RfLogBus idLogBus;
    @Column(nullable = false)
    private String photos;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;

    public RfSuivisMecanique() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OpSignalementGenerale getIdSignalement() {
        return idSignalement;
    }

    public void setIdSignalement(OpSignalementGenerale idSignalement) {
        this.idSignalement = idSignalement;
    }

    public RfLogBus getIdLogBus() {
        return idLogBus;
    }

    public void setIdLogBus(RfLogBus idLogBus) {
        this.idLogBus = idLogBus;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
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

    public SysUsers getIdUserCreatedAt() {
        return idUserCreatedAt;
    }

    public void setIdUserCreatedAt(SysUsers idUserCreatedAt) {
        this.idUserCreatedAt = idUserCreatedAt;
    }
}
