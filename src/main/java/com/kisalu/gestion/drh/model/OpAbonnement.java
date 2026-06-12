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


import com.kisalu.gestion.drh.model.RfService;
import com.kisalu.gestion.drh.model.SysCurrency;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_abonnement")
public class OpAbonnement {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private RfService serviceId;
    @Column(name = "Type_abonnement", nullable = false)
    private String typeAbonnement;
    @Column(name = "Duree_abonnement", nullable = false)
    private String dureeAbonnement;
    @Column(nullable = false)
    private Double prix;
    @Column(name = "date_abonnement", nullable = false)
    private LocalDateTime dateAbonnement;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_currency")
    private SysCurrency idCurrency;
    @Column(name = "fixed_courses_abo", nullable = false)
    private Integer fixedCoursesAbo;
    @Column(nullable = false)
    private String status;

    public OpAbonnement() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RfService getServiceId() {
        return serviceId;
    }

    public void setServiceId(RfService serviceId) {
        this.serviceId = serviceId;
    }

    public String getTypeAbonnement() {
        return typeAbonnement;
    }

    public void setTypeAbonnement(String typeAbonnement) {
        this.typeAbonnement = typeAbonnement;
    }

    public String getDureeAbonnement() {
        return dureeAbonnement;
    }

    public void setDureeAbonnement(String dureeAbonnement) {
        this.dureeAbonnement = dureeAbonnement;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public LocalDateTime getDateAbonnement() {
        return dateAbonnement;
    }

    public void setDateAbonnement(LocalDateTime dateAbonnement) {
        this.dateAbonnement = dateAbonnement;
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

    public SysUsers getIdUserCreatedAt() {
        return idUserCreatedAt;
    }

    public void setIdUserCreatedAt(SysUsers idUserCreatedAt) {
        this.idUserCreatedAt = idUserCreatedAt;
    }

    public SysCurrency getIdCurrency() {
        return idCurrency;
    }

    public void setIdCurrency(SysCurrency idCurrency) {
        this.idCurrency = idCurrency;
    }

    public Integer getFixedCoursesAbo() {
        return fixedCoursesAbo;
    }

    public void setFixedCoursesAbo(Integer fixedCoursesAbo) {
        this.fixedCoursesAbo = fixedCoursesAbo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
