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


import com.kisalu.gestion.drh.model.OpAgent;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "rf_historique_update")
public class RfHistoriqueUpdate {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_op_agent_autorise", referencedColumnName = "code_generate_tac", nullable = true)
    private OpAgent idOpAgentAutorise;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_op_agent_modifie")
    private OpAgent idOpAgentModifie;
    @Column(name = "table_modifie", nullable = false)
    private String tableModifie;
    @Column(name = "id_ligne_modifie", nullable = false)
    private Integer idLigneModifie;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created", referencedColumnName = "id_generate")
    private SysUsers idUserCreated;

    public RfHistoriqueUpdate() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OpAgent getIdOpAgentAutorise() {
        return idOpAgentAutorise;
    }

    public void setIdOpAgentAutorise(OpAgent idOpAgentAutorise) {
        this.idOpAgentAutorise = idOpAgentAutorise;
    }

    public OpAgent getIdOpAgentModifie() {
        return idOpAgentModifie;
    }

    public void setIdOpAgentModifie(OpAgent idOpAgentModifie) {
        this.idOpAgentModifie = idOpAgentModifie;
    }

    public String getTableModifie() {
        return tableModifie;
    }

    public void setTableModifie(String tableModifie) {
        this.tableModifie = tableModifie;
    }

    public Integer getIdLigneModifie() {
        return idLigneModifie;
    }

    public void setIdLigneModifie(Integer idLigneModifie) {
        this.idLigneModifie = idLigneModifie;
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

    public SysUsers getIdUserCreated() {
        return idUserCreated;
    }

    public void setIdUserCreated(SysUsers idUserCreated) {
        this.idUserCreated = idUserCreated;
    }
}
