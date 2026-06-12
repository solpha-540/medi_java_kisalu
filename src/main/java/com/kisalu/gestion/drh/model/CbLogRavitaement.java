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


import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "cb_log_ravitaement")
public class CbLogRavitaement {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "type_balance", nullable = false)
    private String typeBalance;
    @Column(name = "solde_courant")
    private BigDecimal soldeCourant;
    @Column(name = "solde_recharger")
    private BigDecimal soldeRecharger;
    private BigDecimal solde;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_beneficiaire", referencedColumnName = "id_generate")
    private SysUsers userBeneficiaire;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id_generate")
    private SysUsers createdByUserId;

    public CbLogRavitaement() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeBalance() {
        return typeBalance;
    }

    public void setTypeBalance(String typeBalance) {
        this.typeBalance = typeBalance;
    }

    public BigDecimal getSoldeCourant() {
        return soldeCourant;
    }

    public void setSoldeCourant(BigDecimal soldeCourant) {
        this.soldeCourant = soldeCourant;
    }

    public BigDecimal getSoldeRecharger() {
        return soldeRecharger;
    }

    public void setSoldeRecharger(BigDecimal soldeRecharger) {
        this.soldeRecharger = soldeRecharger;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public SysUsers getUserBeneficiaire() {
        return userBeneficiaire;
    }

    public void setUserBeneficiaire(SysUsers userBeneficiaire) {
        this.userBeneficiaire = userBeneficiaire;
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

    public SysUsers getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(SysUsers createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
