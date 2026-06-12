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
import com.kisalu.gestion.drh.model.OpEtudiant;
import com.kisalu.gestion.drh.model.RfBus;
import com.kisalu.gestion.drh.model.RfLignes;

@Entity
@Table(name = "op_course")
public class OpCourse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "libele_course", nullable = false)
    private String libeleCourse;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_agent", referencedColumnName = "code_generate_tac", nullable = true)
    private OpAgent idAgent;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_ligneTrajectoire", nullable = true)
    private RfLignes idLignetrajectoire;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_bus")
    private RfBus idBus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etudiant")
    private OpEtudiant idEtudiant;
    @Column(name = "status_courses", nullable = false)
    private String statusCourses;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    public OpCourse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibeleCourse() {
        return libeleCourse;
    }

    public void setLibeleCourse(String libeleCourse) {
        this.libeleCourse = libeleCourse;
    }

    public OpAgent getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(OpAgent idAgent) {
        this.idAgent = idAgent;
    }

    public RfLignes getIdLignetrajectoire() {
        return idLignetrajectoire;
    }

    public void setIdLignetrajectoire(RfLignes idLignetrajectoire) {
        this.idLignetrajectoire = idLignetrajectoire;
    }

    public RfBus getIdBus() {
        return idBus;
    }

    public void setIdBus(RfBus idBus) {
        this.idBus = idBus;
    }

    public OpEtudiant getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(OpEtudiant idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public String getStatusCourses() {
        return statusCourses;
    }

    public void setStatusCourses(String statusCourses) {
        this.statusCourses = statusCourses;
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
}
