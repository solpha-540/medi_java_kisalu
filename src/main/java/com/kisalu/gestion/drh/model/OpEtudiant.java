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


import com.kisalu.gestion.drh.model.OpAdresse;
import com.kisalu.gestion.drh.model.RfEtudiantIdentification;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_etudiant")
public class OpEtudiant {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "code_generate_tac", nullable = false)
    private String codeGenerateTac;
    private String nom;
    private String prenom;
    private String postnom;
    private String sexe;
    @Column(name = "lieuNaissance")
    private String lieunaissance;
    @Column(name = "Telephone")
    private String telephone;
    private String matricule;
    private String avenue;
    private String numero;
    private String email;
    private String photo;
    @Column(name = "DateNaissance")
    private LocalDate datenaissance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_etudiant_identification", nullable = true)
    private RfEtudiantIdentification idEtudiantIdentification;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_adresse", nullable = true)
    private OpAdresse idAdresse;
    private String source;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;

    public OpEtudiant() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodeGenerateTac() {
        return codeGenerateTac;
    }

    public void setCodeGenerateTac(String codeGenerateTac) {
        this.codeGenerateTac = codeGenerateTac;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPostnom() {
        return postnom;
    }

    public void setPostnom(String postnom) {
        this.postnom = postnom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getLieunaissance() {
        return lieunaissance;
    }

    public void setLieunaissance(String lieunaissance) {
        this.lieunaissance = lieunaissance;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getAvenue() {
        return avenue;
    }

    public void setAvenue(String avenue) {
        this.avenue = avenue;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDate getDatenaissance() {
        return datenaissance;
    }

    public void setDatenaissance(LocalDate datenaissance) {
        this.datenaissance = datenaissance;
    }

    public RfEtudiantIdentification getIdEtudiantIdentification() {
        return idEtudiantIdentification;
    }

    public void setIdEtudiantIdentification(RfEtudiantIdentification idEtudiantIdentification) {
        this.idEtudiantIdentification = idEtudiantIdentification;
    }

    public OpAdresse getIdAdresse() {
        return idAdresse;
    }

    public void setIdAdresse(OpAdresse idAdresse) {
        this.idAdresse = idAdresse;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
