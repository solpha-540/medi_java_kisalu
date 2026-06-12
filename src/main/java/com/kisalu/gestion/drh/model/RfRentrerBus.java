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


@Entity
@Table(name = "rf_rentrerBus")
public class RfRentrerBus {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "chauffeur_id")
    private String chauffeurId;
    @Column(name = "verificateur_id")
    private String verificateurId;
    @Column(name = "encadreur_id")
    private String encadreurId;
    @Column(name = "convoyeur_id")
    private String convoyeurId;
    @Column(nullable = false)
    private String observation;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @Column(name = "id_user_created", nullable = false)
    private String idUserCreated;
    @Column(name = "bus_id", nullable = false)
    private Integer busId;
    @Column(name = "ligne_id", nullable = false)
    private Integer ligneId;

    public RfRentrerBus() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChauffeurId() {
        return chauffeurId;
    }

    public void setChauffeurId(String chauffeurId) {
        this.chauffeurId = chauffeurId;
    }

    public String getVerificateurId() {
        return verificateurId;
    }

    public void setVerificateurId(String verificateurId) {
        this.verificateurId = verificateurId;
    }

    public String getEncadreurId() {
        return encadreurId;
    }

    public void setEncadreurId(String encadreurId) {
        this.encadreurId = encadreurId;
    }

    public String getConvoyeurId() {
        return convoyeurId;
    }

    public void setConvoyeurId(String convoyeurId) {
        this.convoyeurId = convoyeurId;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
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

    public String getIdUserCreated() {
        return idUserCreated;
    }

    public void setIdUserCreated(String idUserCreated) {
        this.idUserCreated = idUserCreated;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public Integer getLigneId() {
        return ligneId;
    }

    public void setLigneId(Integer ligneId) {
        this.ligneId = ligneId;
    }
}
