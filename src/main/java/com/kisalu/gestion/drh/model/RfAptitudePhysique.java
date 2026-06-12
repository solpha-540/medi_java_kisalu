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

@Entity
@Table(name = "rf_aptitude_physique")
public class RfAptitudePhysique {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "groupe_sanguin", nullable = false)
    private String groupeSanguin;
    @Column(nullable = false)
    private String allerigie;
    @Column(name = "etat_de_sante", nullable = false)
    private String etatDeSante;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_op_agent")
    private OpAgent idOpAgent;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    @Column(name = "id_user_created", nullable = false)
    private String idUserCreated;

    public RfAptitudePhysique() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public String getAllerigie() {
        return allerigie;
    }

    public void setAllerigie(String allerigie) {
        this.allerigie = allerigie;
    }

    public String getEtatDeSante() {
        return etatDeSante;
    }

    public void setEtatDeSante(String etatDeSante) {
        this.etatDeSante = etatDeSante;
    }

    public OpAgent getIdOpAgent() {
        return idOpAgent;
    }

    public void setIdOpAgent(OpAgent idOpAgent) {
        this.idOpAgent = idOpAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getIdUserCreated() {
        return idUserCreated;
    }

    public void setIdUserCreated(String idUserCreated) {
        this.idUserCreated = idUserCreated;
    }
}
