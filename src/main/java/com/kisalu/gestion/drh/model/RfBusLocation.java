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
@Table(name = "rf_bus_location")
public class RfBusLocation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "type_bus", nullable = false)
    private String typeBus;
    @Column(nullable = false)
    private String categorie;
    @Column(name = "nombre_bus", nullable = false)
    private Integer nombreBus;
    @Column(name = "bus_reste", nullable = false)
    private Integer busReste;
    @Column(nullable = false)
    private Integer capacite;
    @Column(nullable = false)
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created", referencedColumnName = "id_generate")
    private SysUsers idUserCreated;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "prix_immobilisation", nullable = false)
    private Double prixImmobilisation;

    public RfBusLocation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeBus() {
        return typeBus;
    }

    public void setTypeBus(String typeBus) {
        this.typeBus = typeBus;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Integer getNombreBus() {
        return nombreBus;
    }

    public void setNombreBus(Integer nombreBus) {
        this.nombreBus = nombreBus;
    }

    public Integer getBusReste() {
        return busReste;
    }

    public void setBusReste(Integer busReste) {
        this.busReste = busReste;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SysUsers getIdUserCreated() {
        return idUserCreated;
    }

    public void setIdUserCreated(SysUsers idUserCreated) {
        this.idUserCreated = idUserCreated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Double getPrixImmobilisation() {
        return prixImmobilisation;
    }

    public void setPrixImmobilisation(Double prixImmobilisation) {
        this.prixImmobilisation = prixImmobilisation;
    }
}
