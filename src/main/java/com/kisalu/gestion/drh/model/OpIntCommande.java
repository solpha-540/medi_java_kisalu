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


import com.kisalu.gestion.drh.model.OpAbonnement;
import com.kisalu.gestion.drh.model.OpAgent;

@Entity
@Table(name = "op_int_commande")
public class OpIntCommande {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommandeId")
    private Integer commandeid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abonnement_id")
    private OpAbonnement abonnementId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Agent_Id", referencedColumnName = "code_generate_tac")
    private OpAgent agentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Agent_Id_Do", referencedColumnName = "code_generate_tac", nullable = true)
    private OpAgent agentIdDo;
    @Column(name = "transaction_rf")
    private String transactionRf;
    @Column(name = "QuantiteCommandee", nullable = false)
    private Integer quantitecommandee;
    @Column(name = "QuantiteLivree")
    private Integer quantitelivree;
    @Column(name = "currency_id")
    private Integer currencyId;
    @Column(name = "Gap")
    private Integer gap;
    @Column(name = "DateCommande", nullable = false)
    private LocalDateTime datecommande;
    @Column(name = "DateDemandee")
    private LocalDateTime datedemandee;
    @Column(name = "DateExpedition")
    private LocalDateTime dateexpedition;
    @Column(name = "TransporteurId")
    private String transporteurid;
    @Column(name = "Status")
    private String status;
    @Column(name = "date_Liv")
    private LocalDateTime dateLiv;
    @Column(name = "date_Trait")
    private LocalDateTime dateTrait;
    @Column(name = "date_Reçu")
    private LocalDateTime dateReçu;

    public OpIntCommande() {
    }

    public Integer getCommandeid() {
        return commandeid;
    }

    public void setCommandeid(Integer commandeid) {
        this.commandeid = commandeid;
    }

    public OpAbonnement getAbonnementId() {
        return abonnementId;
    }

    public void setAbonnementId(OpAbonnement abonnementId) {
        this.abonnementId = abonnementId;
    }

    public OpAgent getAgentId() {
        return agentId;
    }

    public void setAgentId(OpAgent agentId) {
        this.agentId = agentId;
    }

    public OpAgent getAgentIdDo() {
        return agentIdDo;
    }

    public void setAgentIdDo(OpAgent agentIdDo) {
        this.agentIdDo = agentIdDo;
    }

    public String getTransactionRf() {
        return transactionRf;
    }

    public void setTransactionRf(String transactionRf) {
        this.transactionRf = transactionRf;
    }

    public Integer getQuantitecommandee() {
        return quantitecommandee;
    }

    public void setQuantitecommandee(Integer quantitecommandee) {
        this.quantitecommandee = quantitecommandee;
    }

    public Integer getQuantitelivree() {
        return quantitelivree;
    }

    public void setQuantitelivree(Integer quantitelivree) {
        this.quantitelivree = quantitelivree;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Integer getGap() {
        return gap;
    }

    public void setGap(Integer gap) {
        this.gap = gap;
    }

    public LocalDateTime getDatecommande() {
        return datecommande;
    }

    public void setDatecommande(LocalDateTime datecommande) {
        this.datecommande = datecommande;
    }

    public LocalDateTime getDatedemandee() {
        return datedemandee;
    }

    public void setDatedemandee(LocalDateTime datedemandee) {
        this.datedemandee = datedemandee;
    }

    public LocalDateTime getDateexpedition() {
        return dateexpedition;
    }

    public void setDateexpedition(LocalDateTime dateexpedition) {
        this.dateexpedition = dateexpedition;
    }

    public String getTransporteurid() {
        return transporteurid;
    }

    public void setTransporteurid(String transporteurid) {
        this.transporteurid = transporteurid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDateLiv() {
        return dateLiv;
    }

    public void setDateLiv(LocalDateTime dateLiv) {
        this.dateLiv = dateLiv;
    }

    public LocalDateTime getDateTrait() {
        return dateTrait;
    }

    public void setDateTrait(LocalDateTime dateTrait) {
        this.dateTrait = dateTrait;
    }
}
