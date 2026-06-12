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
import com.kisalu.gestion.drh.model.OpEtudiant;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "cb_check_courses")
public class CbCheckCourses {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code_generate_tac", nullable = false)
    private String codeGenerateTac;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_op_etudiant")
    private OpEtudiant idOpEtudiant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_abonnement", nullable = true)
    private OpAbonnement idAbonnement;
    @Column(name = "date_expired_abo")
    private LocalDateTime dateExpiredAbo;
    @Column(name = "date_abonnement")
    private LocalDateTime dateAbonnement;
    @Column(name = "validity_abonnement")
    private Integer validityAbonnement;
    @Column(name = "status_day")
    private String statusDay;
    @Column(name = "status_month")
    private String statusMonth;
    @Column(name = "count_courses_day")
    private Integer countCoursesDay;
    @Column(name = "fixed_courses_abo")
    private Integer fixedCoursesAbo;
    @Column(name = "date_update")
    private LocalDateTime dateUpdate;
    @Column(name = "update_usage_courses")
    private LocalDateTime updateUsageCourses;
    @Column(name = "last_refresh_courses")
    private LocalDateTime lastRefreshCourses;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;

    public CbCheckCourses() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodeGenerateTac() {
        return codeGenerateTac;
    }

    public void setCodeGenerateTac(String codeGenerateTac) {
        this.codeGenerateTac = codeGenerateTac;
    }

    public OpEtudiant getIdOpEtudiant() {
        return idOpEtudiant;
    }

    public void setIdOpEtudiant(OpEtudiant idOpEtudiant) {
        this.idOpEtudiant = idOpEtudiant;
    }

    public OpAbonnement getIdAbonnement() {
        return idAbonnement;
    }

    public void setIdAbonnement(OpAbonnement idAbonnement) {
        this.idAbonnement = idAbonnement;
    }

    public LocalDateTime getDateExpiredAbo() {
        return dateExpiredAbo;
    }

    public void setDateExpiredAbo(LocalDateTime dateExpiredAbo) {
        this.dateExpiredAbo = dateExpiredAbo;
    }

    public LocalDateTime getDateAbonnement() {
        return dateAbonnement;
    }

    public void setDateAbonnement(LocalDateTime dateAbonnement) {
        this.dateAbonnement = dateAbonnement;
    }

    public Integer getValidityAbonnement() {
        return validityAbonnement;
    }

    public void setValidityAbonnement(Integer validityAbonnement) {
        this.validityAbonnement = validityAbonnement;
    }

    public String getStatusDay() {
        return statusDay;
    }

    public void setStatusDay(String statusDay) {
        this.statusDay = statusDay;
    }

    public String getStatusMonth() {
        return statusMonth;
    }

    public void setStatusMonth(String statusMonth) {
        this.statusMonth = statusMonth;
    }

    public Integer getCountCoursesDay() {
        return countCoursesDay;
    }

    public void setCountCoursesDay(Integer countCoursesDay) {
        this.countCoursesDay = countCoursesDay;
    }

    public Integer getFixedCoursesAbo() {
        return fixedCoursesAbo;
    }

    public void setFixedCoursesAbo(Integer fixedCoursesAbo) {
        this.fixedCoursesAbo = fixedCoursesAbo;
    }

    public LocalDateTime getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(LocalDateTime dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public LocalDateTime getUpdateUsageCourses() {
        return updateUsageCourses;
    }

    public void setUpdateUsageCourses(LocalDateTime updateUsageCourses) {
        this.updateUsageCourses = updateUsageCourses;
    }

    public LocalDateTime getLastRefreshCourses() {
        return lastRefreshCourses;
    }

    public void setLastRefreshCourses(LocalDateTime lastRefreshCourses) {
        this.lastRefreshCourses = lastRefreshCourses;
    }

    public SysUsers getIdUserCreatedAt() {
        return idUserCreatedAt;
    }

    public void setIdUserCreatedAt(SysUsers idUserCreatedAt) {
        this.idUserCreatedAt = idUserCreatedAt;
    }
}
