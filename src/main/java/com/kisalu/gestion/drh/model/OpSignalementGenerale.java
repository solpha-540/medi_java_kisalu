package com.kisalu.gestion.drh.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import com.kisalu.gestion.drh.model.OpSignalementCarrosserie;
import com.kisalu.gestion.drh.model.OpSignalementElectricite;
import com.kisalu.gestion.drh.model.OpSignalementFluide;
import com.kisalu.gestion.drh.model.OpSignalementMecanique;
import com.kisalu.gestion.drh.model.OpSignalementPneumatique;
import com.kisalu.gestion.drh.model.OpSignalementSecuritePassive;
import com.kisalu.gestion.drh.model.RfBus;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_signalement_generale")
public class OpSignalementGenerale {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bus")
    private RfBus idBus;
    @Column(nullable = false)
    private String carburant;
    @Column(nullable = false)
    private String kilometrage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_signalement_securite_passive")
    private OpSignalementSecuritePassive idSignalementSecuritePassive;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_signalement_mecanique")
    private OpSignalementMecanique idSignalementMecanique;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_signalement_carrosserie")
    private OpSignalementCarrosserie idSignalementCarrosserie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_signalement_pneumatique")
    private OpSignalementPneumatique idSignalementPneumatique;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_signalement_electricite")
    private OpSignalementElectricite idSignalementElectricite;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_signalement_fluide")
    private OpSignalementFluide idSignalementFluide;
    @Column(nullable = false)
    private Integer valider;
    @Column(name = "heure_service", nullable = false)
    private LocalTime heureService;
    @Column(name = "cloture_service")
    private Integer clotureService;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created", referencedColumnName = "id_generate")
    private SysUsers idUserCreated;
    @Column(name = "id_session", nullable = false)
    private Integer idSession;

    public OpSignalementGenerale() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RfBus getIdBus() {
        return idBus;
    }

    public void setIdBus(RfBus idBus) {
        this.idBus = idBus;
    }

    public String getCarburant() {
        return carburant;
    }

    public void setCarburant(String carburant) {
        this.carburant = carburant;
    }

    public String getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(String kilometrage) {
        this.kilometrage = kilometrage;
    }

    public OpSignalementSecuritePassive getIdSignalementSecuritePassive() {
        return idSignalementSecuritePassive;
    }

    public void setIdSignalementSecuritePassive(OpSignalementSecuritePassive idSignalementSecuritePassive) {
        this.idSignalementSecuritePassive = idSignalementSecuritePassive;
    }

    public OpSignalementMecanique getIdSignalementMecanique() {
        return idSignalementMecanique;
    }

    public void setIdSignalementMecanique(OpSignalementMecanique idSignalementMecanique) {
        this.idSignalementMecanique = idSignalementMecanique;
    }

    public OpSignalementCarrosserie getIdSignalementCarrosserie() {
        return idSignalementCarrosserie;
    }

    public void setIdSignalementCarrosserie(OpSignalementCarrosserie idSignalementCarrosserie) {
        this.idSignalementCarrosserie = idSignalementCarrosserie;
    }

    public OpSignalementPneumatique getIdSignalementPneumatique() {
        return idSignalementPneumatique;
    }

    public void setIdSignalementPneumatique(OpSignalementPneumatique idSignalementPneumatique) {
        this.idSignalementPneumatique = idSignalementPneumatique;
    }

    public OpSignalementElectricite getIdSignalementElectricite() {
        return idSignalementElectricite;
    }

    public void setIdSignalementElectricite(OpSignalementElectricite idSignalementElectricite) {
        this.idSignalementElectricite = idSignalementElectricite;
    }

    public OpSignalementFluide getIdSignalementFluide() {
        return idSignalementFluide;
    }

    public void setIdSignalementFluide(OpSignalementFluide idSignalementFluide) {
        this.idSignalementFluide = idSignalementFluide;
    }

    public Integer getValider() {
        return valider;
    }

    public void setValider(Integer valider) {
        this.valider = valider;
    }

    public LocalTime getHeureService() {
        return heureService;
    }

    public void setHeureService(LocalTime heureService) {
        this.heureService = heureService;
    }

    public Integer getClotureService() {
        return clotureService;
    }

    public void setClotureService(Integer clotureService) {
        this.clotureService = clotureService;
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

    public Integer getIdSession() {
        return idSession;
    }

    public void setIdSession(Integer idSession) {
        this.idSession = idSession;
    }
}
