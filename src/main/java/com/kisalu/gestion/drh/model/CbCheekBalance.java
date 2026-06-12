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


import com.kisalu.gestion.drh.model.OpBalance;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "cb_cheek_balance")
public class CbCheekBalance {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id")
    private OpBalance balanceId;
    @Column(name = "transaction_rf")
    private String transactionRf;
    @Column(nullable = false)
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id_generate")
    private SysUsers createdByUserId;
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CbCheekBalance() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OpBalance getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(OpBalance balanceId) {
        this.balanceId = balanceId;
    }

    public String getTransactionRf() {
        return transactionRf;
    }

    public void setTransactionRf(String transactionRf) {
        this.transactionRf = transactionRf;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SysUsers getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(SysUsers createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
