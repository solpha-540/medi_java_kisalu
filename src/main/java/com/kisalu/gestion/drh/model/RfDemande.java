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

@Entity
@Table(name = "rf_demande")
public class RfDemande {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "type_demande", nullable = false)
    private String typeDemande;
    @Column(nullable = false)
    private String message;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agent_demandeur")
    private OpAgent idAgentDemandeur;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agent_recepteur")
    private OpAgent idAgentRecepteur;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private String statut;

    public RfDemande() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeDemande() {
        return typeDemande;
    }

    public void setTypeDemande(String typeDemande) {
        this.typeDemande = typeDemande;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OpAgent getIdAgentDemandeur() {
        return idAgentDemandeur;
    }

    public void setIdAgentDemandeur(OpAgent idAgentDemandeur) {
        this.idAgentDemandeur = idAgentDemandeur;
    }

    public OpAgent getIdAgentRecepteur() {
        return idAgentRecepteur;
    }

    public void setIdAgentRecepteur(OpAgent idAgentRecepteur) {
        this.idAgentRecepteur = idAgentRecepteur;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
