package com.kisalu.gestion.drh.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
@Table(name = "rf_reservation")
public class RfReservation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "tarif_id", nullable = false)
    private Integer tarifId;
    @Column(name = "collation_id")
    private Integer collationId;
    @Column(name = "client_id", nullable = false)
    private Integer clientId;
    @Column(name = "is_collation", nullable = false)
    private Integer isCollation;
    @Column(name = "prix_collation", nullable = false)
    private Integer prixCollation;
    @Column(name = "nombre_bus", nullable = false)
    private Integer nombreBus;
    @Column(name = "preuve_paiement")
    private String preuvePaiement;
    @Column(name = "heure_depart", nullable = false)
    private LocalTime heureDepart;
    @Column(name = "date_depart", nullable = false)
    private LocalDateTime dateDepart;
    @Column(nullable = false)
    private String note;
    @Column(nullable = false)
    private String status;
    @Column(name = "id_user_created")
    private String idUserCreated;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_discount")
    private Integer isDiscount;
    @Column(name = "total_payement")
    private Double totalPayement;
    @Column(name = "pourcentage_discount")
    private Double pourcentageDiscount;
    @Column(name = "is_immobilization")
    private Integer isImmobilization;
    @Column(name = "nombre_personnes")
    private Integer nombrePersonnes;
    @Column(name = "total_reduction")
    private BigDecimal totalReduction;

    public RfReservation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTarifId() {
        return tarifId;
    }

    public void setTarifId(Integer tarifId) {
        this.tarifId = tarifId;
    }

    public Integer getCollationId() {
        return collationId;
    }

    public void setCollationId(Integer collationId) {
        this.collationId = collationId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getIsCollation() {
        return isCollation;
    }

    public void setIsCollation(Integer isCollation) {
        this.isCollation = isCollation;
    }

    public Integer getPrixCollation() {
        return prixCollation;
    }

    public void setPrixCollation(Integer prixCollation) {
        this.prixCollation = prixCollation;
    }

    public Integer getNombreBus() {
        return nombreBus;
    }

    public void setNombreBus(Integer nombreBus) {
        this.nombreBus = nombreBus;
    }

    public String getPreuvePaiement() {
        return preuvePaiement;
    }

    public void setPreuvePaiement(String preuvePaiement) {
        this.preuvePaiement = preuvePaiement;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public LocalDateTime getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDateTime dateDepart) {
        this.dateDepart = dateDepart;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdUserCreated() {
        return idUserCreated;
    }

    public void setIdUserCreated(String idUserCreated) {
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

    public Integer getIsDiscount() {
        return isDiscount;
    }

    public void setIsDiscount(Integer isDiscount) {
        this.isDiscount = isDiscount;
    }

    public Double getTotalPayement() {
        return totalPayement;
    }

    public void setTotalPayement(Double totalPayement) {
        this.totalPayement = totalPayement;
    }

    public Double getPourcentageDiscount() {
        return pourcentageDiscount;
    }

    public void setPourcentageDiscount(Double pourcentageDiscount) {
        this.pourcentageDiscount = pourcentageDiscount;
    }

    public Integer getIsImmobilization() {
        return isImmobilization;
    }

    public void setIsImmobilization(Integer isImmobilization) {
        this.isImmobilization = isImmobilization;
    }

    public Integer getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(Integer nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public BigDecimal getTotalReduction() {
        return totalReduction;
    }

    public void setTotalReduction(BigDecimal totalReduction) {
        this.totalReduction = totalReduction;
    }
}
