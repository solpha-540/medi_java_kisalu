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


import com.kisalu.gestion.drh.model.RfOperateurPaiement;
import com.kisalu.gestion.drh.model.RfService;
import com.kisalu.gestion.drh.model.SysCurrency;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_transaction")
public class OpTransaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_op")
    private RfOperateurPaiement idOp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_currency")
    private SysCurrency idCurrency;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_abonnement")
    private RfService idAbonnement;
    @Column(name = "libele_tac")
    private String libeleTac;
    @Column(name = "status_tac")
    private String statusTac;
    private String amount;
    @Column(name = "walletID")
    private String walletid;
    private String provider;
    @Column(name = "statusCode")
    private String statuscode;
    private String status;
    @Column(name = "transactionReference")
    private String transactionreference;
    @Column(name = "transactionDate")
    private LocalDateTime transactiondate;
    @Column(name = "transactionDescription")
    private String transactiondescription;
    @Column(name = "transactionId")
    private String transactionid;
    private String source;
    private String bordereau;
    @Column(name = "source_activation")
    private String sourceActivation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_user_buy_at", referencedColumnName = "id_generate", nullable = true)
    private SysUsers idUserBuyAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;

    public OpTransaction() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RfOperateurPaiement getIdOp() {
        return idOp;
    }

    public void setIdOp(RfOperateurPaiement idOp) {
        this.idOp = idOp;
    }

    public SysCurrency getIdCurrency() {
        return idCurrency;
    }

    public void setIdCurrency(SysCurrency idCurrency) {
        this.idCurrency = idCurrency;
    }

    public RfService getIdAbonnement() {
        return idAbonnement;
    }

    public void setIdAbonnement(RfService idAbonnement) {
        this.idAbonnement = idAbonnement;
    }

    public String getLibeleTac() {
        return libeleTac;
    }

    public void setLibeleTac(String libeleTac) {
        this.libeleTac = libeleTac;
    }

    public String getStatusTac() {
        return statusTac;
    }

    public void setStatusTac(String statusTac) {
        this.statusTac = statusTac;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWalletid() {
        return walletid;
    }

    public void setWalletid(String walletid) {
        this.walletid = walletid;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(String statuscode) {
        this.statuscode = statuscode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionreference() {
        return transactionreference;
    }

    public void setTransactionreference(String transactionreference) {
        this.transactionreference = transactionreference;
    }

    public LocalDateTime getTransactiondate() {
        return transactiondate;
    }

    public void setTransactiondate(LocalDateTime transactiondate) {
        this.transactiondate = transactiondate;
    }

    public String getTransactiondescription() {
        return transactiondescription;
    }

    public void setTransactiondescription(String transactiondescription) {
        this.transactiondescription = transactiondescription;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBordereau() {
        return bordereau;
    }

    public void setBordereau(String bordereau) {
        this.bordereau = bordereau;
    }

    public String getSourceActivation() {
        return sourceActivation;
    }

    public void setSourceActivation(String sourceActivation) {
        this.sourceActivation = sourceActivation;
    }

    public SysUsers getIdUserBuyAt() {
        return idUserBuyAt;
    }

    public void setIdUserBuyAt(SysUsers idUserBuyAt) {
        this.idUserBuyAt = idUserBuyAt;
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
}
