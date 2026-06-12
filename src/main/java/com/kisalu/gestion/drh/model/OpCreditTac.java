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
@Table(name = "op_credit_tac")
public class OpCreditTac {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "solde_generate_abonement")
    private BigDecimal soldeGenerateAbonement;
    @Column(name = "solde_reste_abonement")
    private BigDecimal soldeResteAbonement;
    @Column(name = "solde_generate_carte")
    private Integer soldeGenerateCarte;
    @Column(name = "solde_reste_carte")
    private Integer soldeResteCarte;
    @Column(name = "ref_credit")
    private String refCredit;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;
    @Column(name = "is_abonement_closed", nullable = false)
    private Integer isAbonementClosed;
    @Column(name = "is_cartes_closed", nullable = false)
    private Integer isCartesClosed;

    public OpCreditTac() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getSoldeGenerateAbonement() {
        return soldeGenerateAbonement;
    }

    public void setSoldeGenerateAbonement(BigDecimal soldeGenerateAbonement) {
        this.soldeGenerateAbonement = soldeGenerateAbonement;
    }

    public BigDecimal getSoldeResteAbonement() {
        return soldeResteAbonement;
    }

    public void setSoldeResteAbonement(BigDecimal soldeResteAbonement) {
        this.soldeResteAbonement = soldeResteAbonement;
    }

    public Integer getSoldeGenerateCarte() {
        return soldeGenerateCarte;
    }

    public void setSoldeGenerateCarte(Integer soldeGenerateCarte) {
        this.soldeGenerateCarte = soldeGenerateCarte;
    }

    public Integer getSoldeResteCarte() {
        return soldeResteCarte;
    }

    public void setSoldeResteCarte(Integer soldeResteCarte) {
        this.soldeResteCarte = soldeResteCarte;
    }

    public String getRefCredit() {
        return refCredit;
    }

    public void setRefCredit(String refCredit) {
        this.refCredit = refCredit;
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

    public SysUsers getIdUserCreatedAt() {
        return idUserCreatedAt;
    }

    public void setIdUserCreatedAt(SysUsers idUserCreatedAt) {
        this.idUserCreatedAt = idUserCreatedAt;
    }

    public Integer getIsAbonementClosed() {
        return isAbonementClosed;
    }

    public void setIsAbonementClosed(Integer isAbonementClosed) {
        this.isAbonementClosed = isAbonementClosed;
    }

    public Integer getIsCartesClosed() {
        return isCartesClosed;
    }

    public void setIsCartesClosed(Integer isCartesClosed) {
        this.isCartesClosed = isCartesClosed;
    }
}
