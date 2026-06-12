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


import com.kisalu.gestion.drh.model.RfDepartement;
import com.kisalu.gestion.drh.model.RfEtablissement;
import com.kisalu.gestion.drh.model.RfFaculte;
import com.kisalu.gestion.drh.model.RfPromotion;

@Entity
@Table(name = "rf_etudiant_identification")
public class RfEtudiantIdentification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "numero_carte")
    private String numeroCarte;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String postnom;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false)
    private String sexe;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_etablissement")
    private RfEtablissement idEtablissement;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_faculte")
    private RfFaculte idFaculte;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_departement")
    private RfDepartement idDepartement;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_promotion")
    private RfPromotion idPromotion;
    @Column(name = "Id_section")
    private Integer idSection;
    @Column(nullable = false)
    private String status;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "Id_user_created_at", nullable = false)
    private String idUserCreatedAt;
    @Column(name = "fichier_carteElecteur", nullable = false)
    private String fichierCarteelecteur;

    public RfEtudiantIdentification() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPostnom() {
        return postnom;
    }

    public void setPostnom(String postnom) {
        this.postnom = postnom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public RfEtablissement getIdEtablissement() {
        return idEtablissement;
    }

    public void setIdEtablissement(RfEtablissement idEtablissement) {
        this.idEtablissement = idEtablissement;
    }

    public RfFaculte getIdFaculte() {
        return idFaculte;
    }

    public void setIdFaculte(RfFaculte idFaculte) {
        this.idFaculte = idFaculte;
    }

    public RfDepartement getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(RfDepartement idDepartement) {
        this.idDepartement = idDepartement;
    }

    public RfPromotion getIdPromotion() {
        return idPromotion;
    }

    public void setIdPromotion(RfPromotion idPromotion) {
        this.idPromotion = idPromotion;
    }

    public Integer getIdSection() {
        return idSection;
    }

    public void setIdSection(Integer idSection) {
        this.idSection = idSection;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getIdUserCreatedAt() {
        return idUserCreatedAt;
    }

    public void setIdUserCreatedAt(String idUserCreatedAt) {
        this.idUserCreatedAt = idUserCreatedAt;
    }

    public String getFichierCarteelecteur() {
        return fichierCarteelecteur;
    }

    public void setFichierCarteelecteur(String fichierCarteelecteur) {
        this.fichierCarteelecteur = fichierCarteelecteur;
    }
}
