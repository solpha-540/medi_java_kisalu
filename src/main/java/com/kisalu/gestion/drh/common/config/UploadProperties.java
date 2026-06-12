package com.kisalu.gestion.drh.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    private String photosAgentsDir = "uploads/photos_agents";
    private String publicPhotosPath = "photos_agents";
    private String actesMariageDir = "uploads/photos_actemariage_agent/actes_mariage";
    private String publicActesMariagePath = "photos_actemariage_agent/actes_mariage";
    private String attestationsDir = "uploads/photos_agents/attestations";
    private String publicAttestationsPath = "photos_agents/attestations";
    private String documentsFormationsDir = "uploads/photos_agents/documents_formations";
    private String publicDocumentsFormationsPath = "photos_agents/documents_formations";
    private String documentJustificatifDir = "uploads/documentJustificatif";
    private String publicDocumentJustificatifPath = "documentJustificatif";
    private String dossiersAgentsDir = "uploads/dossiers_agents";
    private String publicDossiersAgentsPath = "dossiers_agents";
    private String specimenDir = "uploads/specimen";
    private String publicSpecimenPath = "specimen";

    public UploadProperties() {
    }

    public String getPhotosAgentsDir() {
        return photosAgentsDir;
    }

    public void setPhotosAgentsDir(String photosAgentsDir) {
        this.photosAgentsDir = photosAgentsDir;
    }

    public String getPublicPhotosPath() {
        return publicPhotosPath;
    }

    public void setPublicPhotosPath(String publicPhotosPath) {
        this.publicPhotosPath = publicPhotosPath;
    }

    public String getActesMariageDir() {
        return actesMariageDir;
    }

    public void setActesMariageDir(String actesMariageDir) {
        this.actesMariageDir = actesMariageDir;
    }

    public String getPublicActesMariagePath() {
        return publicActesMariagePath;
    }

    public void setPublicActesMariagePath(String publicActesMariagePath) {
        this.publicActesMariagePath = publicActesMariagePath;
    }

    public String getAttestationsDir() {
        return attestationsDir;
    }

    public void setAttestationsDir(String attestationsDir) {
        this.attestationsDir = attestationsDir;
    }

    public String getPublicAttestationsPath() {
        return publicAttestationsPath;
    }

    public void setPublicAttestationsPath(String publicAttestationsPath) {
        this.publicAttestationsPath = publicAttestationsPath;
    }

    public String getDocumentsFormationsDir() {
        return documentsFormationsDir;
    }

    public void setDocumentsFormationsDir(String documentsFormationsDir) {
        this.documentsFormationsDir = documentsFormationsDir;
    }

    public String getPublicDocumentsFormationsPath() {
        return publicDocumentsFormationsPath;
    }

    public void setPublicDocumentsFormationsPath(String publicDocumentsFormationsPath) {
        this.publicDocumentsFormationsPath = publicDocumentsFormationsPath;
    }

    public String getDocumentJustificatifDir() {
        return documentJustificatifDir;
    }

    public void setDocumentJustificatifDir(String documentJustificatifDir) {
        this.documentJustificatifDir = documentJustificatifDir;
    }

    public String getPublicDocumentJustificatifPath() {
        return publicDocumentJustificatifPath;
    }

    public void setPublicDocumentJustificatifPath(String publicDocumentJustificatifPath) {
        this.publicDocumentJustificatifPath = publicDocumentJustificatifPath;
    }

    public String getDossiersAgentsDir() {
        return dossiersAgentsDir;
    }

    public void setDossiersAgentsDir(String dossiersAgentsDir) {
        this.dossiersAgentsDir = dossiersAgentsDir;
    }

    public String getPublicDossiersAgentsPath() {
        return publicDossiersAgentsPath;
    }

    public void setPublicDossiersAgentsPath(String publicDossiersAgentsPath) {
        this.publicDossiersAgentsPath = publicDossiersAgentsPath;
    }

    public String getSpecimenDir() {
        return specimenDir;
    }

    public void setSpecimenDir(String specimenDir) {
        this.specimenDir = specimenDir;
    }

    public String getPublicSpecimenPath() {
        return publicSpecimenPath;
    }

    public void setPublicSpecimenPath(String publicSpecimenPath) {
        this.publicSpecimenPath = publicSpecimenPath;
    }
}
