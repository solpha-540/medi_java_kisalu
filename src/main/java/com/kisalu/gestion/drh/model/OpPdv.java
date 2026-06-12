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
import com.kisalu.gestion.drh.model.OpPartenaires;
import com.kisalu.gestion.drh.model.RfPersonnes;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_pdv")
public class OpPdv {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partenaire_id")
    private OpPartenaires partenaireId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personne_id")
    private RfPersonnes personneId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SysUsers userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id", nullable = true)
    private OpBalance balanceId;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id_generate")
    private SysUsers createdByUserId;

    public OpPdv() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OpPartenaires getPartenaireId() {
        return partenaireId;
    }

    public void setPartenaireId(OpPartenaires partenaireId) {
        this.partenaireId = partenaireId;
    }

    public RfPersonnes getPersonneId() {
        return personneId;
    }

    public void setPersonneId(RfPersonnes personneId) {
        this.personneId = personneId;
    }

    public SysUsers getUserId() {
        return userId;
    }

    public void setUserId(SysUsers userId) {
        this.userId = userId;
    }

    public OpBalance getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(OpBalance balanceId) {
        this.balanceId = balanceId;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public SysUsers getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(SysUsers createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
