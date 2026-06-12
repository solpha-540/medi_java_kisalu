package com.kisalu.gestion.drh.model;

import java.math.BigDecimal;
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


import com.kisalu.gestion.drh.model.SysUsers;

@Entity
@Table(name = "sys_currency")
public class SysCurrency {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(name = "format_key", nullable = false)
    private String formatKey;
    @Column(nullable = false)
    private String symbol;
    @Column(name = "taux_change", nullable = false)
    private Integer tauxChange;
    @Column(name = "min_monentary_unit", nullable = false)
    private BigDecimal minMonentaryUnit;
    @Column(name = "iso_format")
    private String isoFormat;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_created_at", referencedColumnName = "id_generate")
    private SysUsers idUserCreatedAt;

    public SysCurrency() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormatKey() {
        return formatKey;
    }

    public void setFormatKey(String formatKey) {
        this.formatKey = formatKey;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getTauxChange() {
        return tauxChange;
    }

    public void setTauxChange(Integer tauxChange) {
        this.tauxChange = tauxChange;
    }

    public BigDecimal getMinMonentaryUnit() {
        return minMonentaryUnit;
    }

    public void setMinMonentaryUnit(BigDecimal minMonentaryUnit) {
        this.minMonentaryUnit = minMonentaryUnit;
    }

    public String getIsoFormat() {
        return isoFormat;
    }

    public void setIsoFormat(String isoFormat) {
        this.isoFormat = isoFormat;
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
}
