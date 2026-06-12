package com.kisalu.gestion.drh.model;

import java.math.BigDecimal;
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


import com.kisalu.gestion.drh.model.RfBusLocation;
import com.kisalu.gestion.drh.model.RfDestination;

@Entity
@Table(name = "rf_tarif")
public class RfTarif {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String durree;
    @Column(nullable = false)
    private Integer prix;
    @Column(name = "caution_pourcentage")
    private BigDecimal cautionPourcentage;
    @Column(name = "caution_montant", nullable = false)
    private Integer cautionMontant;
    @Column(nullable = false)
    private Integer total;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private RfDestination destinationId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_location_id")
    private RfBusLocation busLocationId;
    @Column(name = "id_user_created", nullable = false)
    private String idUserCreated;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public RfTarif() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDurree() {
        return durree;
    }

    public void setDurree(String durree) {
        this.durree = durree;
    }

    public Integer getPrix() {
        return prix;
    }

    public void setPrix(Integer prix) {
        this.prix = prix;
    }

    public BigDecimal getCautionPourcentage() {
        return cautionPourcentage;
    }

    public void setCautionPourcentage(BigDecimal cautionPourcentage) {
        this.cautionPourcentage = cautionPourcentage;
    }

    public Integer getCautionMontant() {
        return cautionMontant;
    }

    public void setCautionMontant(Integer cautionMontant) {
        this.cautionMontant = cautionMontant;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public RfDestination getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(RfDestination destinationId) {
        this.destinationId = destinationId;
    }

    public RfBusLocation getBusLocationId() {
        return busLocationId;
    }

    public void setBusLocationId(RfBusLocation busLocationId) {
        this.busLocationId = busLocationId;
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
}
