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
import com.kisalu.gestion.drh.model.RfBus;
import com.kisalu.gestion.drh.model.RfLignes;
import com.kisalu.gestion.drh.model.RfSession;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_affectation_reseaux")
public class OpAffectationReseaux {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bus")
    private RfBus idBus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chauffeur", nullable = true)
    private OpAgent idChauffeur;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_verificateur", nullable = true)
    private OpAgent idVerificateur;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_convoyeur", nullable = true)
    private OpAgent idConvoyeur;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_encadreur", nullable = true)
    private OpAgent idEncadreur;
    @Column(name = "is_valide", nullable = false)
    private Integer isValide;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lignes")
    private RfLignes idLignes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session", nullable = true)
    private RfSession idSession;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created", referencedColumnName = "id_generate")
    private SysUsers idUserCreated;

    public OpAffectationReseaux() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RfBus getIdBus() {
        return idBus;
    }

    public void setIdBus(RfBus idBus) {
        this.idBus = idBus;
    }

    public OpAgent getIdChauffeur() {
        return idChauffeur;
    }

    public void setIdChauffeur(OpAgent idChauffeur) {
        this.idChauffeur = idChauffeur;
    }

    public OpAgent getIdVerificateur() {
        return idVerificateur;
    }

    public void setIdVerificateur(OpAgent idVerificateur) {
        this.idVerificateur = idVerificateur;
    }

    public OpAgent getIdConvoyeur() {
        return idConvoyeur;
    }

    public void setIdConvoyeur(OpAgent idConvoyeur) {
        this.idConvoyeur = idConvoyeur;
    }

    public OpAgent getIdEncadreur() {
        return idEncadreur;
    }

    public void setIdEncadreur(OpAgent idEncadreur) {
        this.idEncadreur = idEncadreur;
    }

    public Integer getIsValide() {
        return isValide;
    }

    public void setIsValide(Integer isValide) {
        this.isValide = isValide;
    }

    public RfLignes getIdLignes() {
        return idLignes;
    }

    public void setIdLignes(RfLignes idLignes) {
        this.idLignes = idLignes;
    }

    public RfSession getIdSession() {
        return idSession;
    }

    public void setIdSession(RfSession idSession) {
        this.idSession = idSession;
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
