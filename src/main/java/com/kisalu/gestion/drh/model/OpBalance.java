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


import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_balance")
public class OpBalance {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "balance_abonnement", nullable = false)
    private Integer balanceAbonnement;
    @Column(name = "commission_abo", nullable = false)
    private Integer commissionAbo;
    @Column(name = "balance_cartes", nullable = false)
    private Integer balanceCartes;
    @Column(name = "commission_carte", nullable = false)
    private Integer commissionCarte;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id_generate")
    private SysUsers createdByUserId;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "total_abonnement", nullable = false)
    private Integer totalAbonnement;
    @Column(name = "total_cartes", nullable = false)
    private Integer totalCartes;

    public OpBalance() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBalanceAbonnement() {
        return balanceAbonnement;
    }

    public void setBalanceAbonnement(Integer balanceAbonnement) {
        this.balanceAbonnement = balanceAbonnement;
    }

    public Integer getCommissionAbo() {
        return commissionAbo;
    }

    public void setCommissionAbo(Integer commissionAbo) {
        this.commissionAbo = commissionAbo;
    }

    public Integer getBalanceCartes() {
        return balanceCartes;
    }

    public void setBalanceCartes(Integer balanceCartes) {
        this.balanceCartes = balanceCartes;
    }

    public Integer getCommissionCarte() {
        return commissionCarte;
    }

    public void setCommissionCarte(Integer commissionCarte) {
        this.commissionCarte = commissionCarte;
    }

    public SysUsers getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(SysUsers createdByUserId) {
        this.createdByUserId = createdByUserId;
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

    public Integer getTotalAbonnement() {
        return totalAbonnement;
    }

    public void setTotalAbonnement(Integer totalAbonnement) {
        this.totalAbonnement = totalAbonnement;
    }

    public Integer getTotalCartes() {
        return totalCartes;
    }

    public void setTotalCartes(Integer totalCartes) {
        this.totalCartes = totalCartes;
    }
}
