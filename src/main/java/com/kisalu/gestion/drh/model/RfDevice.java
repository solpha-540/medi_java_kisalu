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
@Table(name = "rf_device")
public class RfDevice {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "IP_MAC", nullable = false)
    private String ipMac;
    @Column(name = "NAME_DEVICE", nullable = false)
    private String nameDevice;
    @Column(name = "IP_IMEI", nullable = false)
    private String ipImei;
    @Column(name = "MODEL_DEVICE", nullable = false)
    private String modelDevice;
    @Column(name = "VERSION_OS", nullable = false)
    private String versionOs;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AGENT", referencedColumnName = "code_generate_tac", nullable = true)
    private OpAgent idAgent;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_connexion", nullable = false)
    private LocalDateTime lastConnexion;
    @Column(name = "app_agent_version")
    private String appAgentVersion;

    public RfDevice() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIpMac() {
        return ipMac;
    }

    public void setIpMac(String ipMac) {
        this.ipMac = ipMac;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public String getIpImei() {
        return ipImei;
    }

    public void setIpImei(String ipImei) {
        this.ipImei = ipImei;
    }

    public String getModelDevice() {
        return modelDevice;
    }

    public void setModelDevice(String modelDevice) {
        this.modelDevice = modelDevice;
    }

    public String getVersionOs() {
        return versionOs;
    }

    public void setVersionOs(String versionOs) {
        this.versionOs = versionOs;
    }

    public OpAgent getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(OpAgent idAgent) {
        this.idAgent = idAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastConnexion() {
        return lastConnexion;
    }

    public void setLastConnexion(LocalDateTime lastConnexion) {
        this.lastConnexion = lastConnexion;
    }

    public String getAppAgentVersion() {
        return appAgentVersion;
    }

    public void setAppAgentVersion(String appAgentVersion) {
        this.appAgentVersion = appAgentVersion;
    }
}
