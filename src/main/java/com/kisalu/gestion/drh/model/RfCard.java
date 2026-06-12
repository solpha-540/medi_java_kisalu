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
@Table(name = "rf_card")
public class RfCard {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(nullable = false)
    private String number;
    @Column(name = "status_activation", nullable = false)
    private String statusActivation;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @Column(name = "activation_date")
    private LocalDateTime activationDate;
    @Column(name = "Id_user_created_at")
    private String idUserCreatedAt;
    @Column(name = "date_attribution")
    private LocalDateTime dateAttribution;
    @Column(name = "status_attribution")
    private String statusAttribution;
    @Column(name = "is_impresion")
    private Integer isImpresion;
    @Column(name = "is_user_attributed")
    private String isUserAttributed;
    @Column(name = "code_reserve")
    private String codeReserve;

    public RfCard() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatusActivation() {
        return statusActivation;
    }

    public void setStatusActivation(String statusActivation) {
        this.statusActivation = statusActivation;
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

    public LocalDateTime getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDateTime activationDate) {
        this.activationDate = activationDate;
    }

    public String getIdUserCreatedAt() {
        return idUserCreatedAt;
    }

    public void setIdUserCreatedAt(String idUserCreatedAt) {
        this.idUserCreatedAt = idUserCreatedAt;
    }

    public LocalDateTime getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(LocalDateTime dateAttribution) {
        this.dateAttribution = dateAttribution;
    }

    public String getStatusAttribution() {
        return statusAttribution;
    }

    public void setStatusAttribution(String statusAttribution) {
        this.statusAttribution = statusAttribution;
    }

    public Integer getIsImpresion() {
        return isImpresion;
    }

    public void setIsImpresion(Integer isImpresion) {
        this.isImpresion = isImpresion;
    }

    public String getIsUserAttributed() {
        return isUserAttributed;
    }

    public void setIsUserAttributed(String isUserAttributed) {
        this.isUserAttributed = isUserAttributed;
    }

    public String getCodeReserve() {
        return codeReserve;
    }

    public void setCodeReserve(String codeReserve) {
        this.codeReserve = codeReserve;
    }
}
