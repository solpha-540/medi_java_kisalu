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


import com.kisalu.gestion.drh.model.RfCategorie;
import com.kisalu.gestion.drh.model.RfCotation;
import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "op_signalement_carrosserie")
public class OpSignalementCarrosserie {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cotation")
    private RfCotation idCotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categorie")
    private RfCategorie idCategorie;
    @Column(nullable = false)
    private String photo;
    @Column(nullable = false)
    private String commentaire;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created", referencedColumnName = "id_generate")
    private SysUsers idUserCreated;

    public OpSignalementCarrosserie() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RfCotation getIdCotation() {
        return idCotation;
    }

    public void setIdCotation(RfCotation idCotation) {
        this.idCotation = idCotation;
    }

    public RfCategorie getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(RfCategorie idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
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
