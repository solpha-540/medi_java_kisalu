package com.kisalu.gestion.drh.model;

import java.time.LocalDate;
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
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "rf_formation")
public class RfFormation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "nom_formation", nullable = false)
    private String nomFormation;
    @Column(nullable = false)
    private String etablissement;
    @Column(nullable = false)
    private String experience;
    @Column(nullable = false)
    private LocalDate annee;
    @Column(nullable = false)
    private String document;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_op_agent")
    private OpAgent idOpAgent;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created", referencedColumnName = "id_generate")
    private SysUsers idUserCreated;
    @Column(nullable = false)
    private String statut;
    private String commentaire;

    public RfFormation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomFormation() {
        return nomFormation;
    }

    public void setNomFormation(String nomFormation) {
        this.nomFormation = nomFormation;
    }

    public String getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(String etablissement) {
        this.etablissement = etablissement;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public LocalDate getAnnee() {
        return annee;
    }

    public void setAnnee(LocalDate annee) {
        this.annee = annee;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public OpAgent getIdOpAgent() {
        return idOpAgent;
    }

    public void setIdOpAgent(OpAgent idOpAgent) {
        this.idOpAgent = idOpAgent;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
