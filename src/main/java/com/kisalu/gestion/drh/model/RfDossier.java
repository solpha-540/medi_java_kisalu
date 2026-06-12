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
@Table(name = "rf_dossier")
public class RfDossier {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_op_agent")
    private OpAgent idOpAgent;
    @Column(name = "curiculum_vitae", nullable = false)
    private String curiculumVitae;
    @Column(name = "diplome_etat", nullable = false)
    private String diplomeEtat;
    @Column(name = "diplome_gradua", nullable = false)
    private String diplomeGradua;
    @Column(name = "diplome_licence", nullable = false)
    private String diplomeLicence;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created", referencedColumnName = "id_generate")
    private SysUsers idUserCreated;

    public RfDossier() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OpAgent getIdOpAgent() {
        return idOpAgent;
    }

    public void setIdOpAgent(OpAgent idOpAgent) {
        this.idOpAgent = idOpAgent;
    }

    public String getCuriculumVitae() {
        return curiculumVitae;
    }

    public void setCuriculumVitae(String curiculumVitae) {
        this.curiculumVitae = curiculumVitae;
    }

    public String getDiplomeEtat() {
        return diplomeEtat;
    }

    public void setDiplomeEtat(String diplomeEtat) {
        this.diplomeEtat = diplomeEtat;
    }

    public String getDiplomeGradua() {
        return diplomeGradua;
    }

    public void setDiplomeGradua(String diplomeGradua) {
        this.diplomeGradua = diplomeGradua;
    }

    public String getDiplomeLicence() {
        return diplomeLicence;
    }

    public void setDiplomeLicence(String diplomeLicence) {
        this.diplomeLicence = diplomeLicence;
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

    public SysUsers getIdUserCreated() {
        return idUserCreated;
    }

    public void setIdUserCreated(SysUsers idUserCreated) {
        this.idUserCreated = idUserCreated;
    }
}
