package com.kisalu.gestion.drh.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/** Sert les photos agents (equivalent PHP : /public/photos_agents/). */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    public StaticResourceConfig(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        register(registry, uploadProperties.getPublicPhotosPath(), uploadProperties.getPhotosAgentsDir());
        register(registry, uploadProperties.getPublicActesMariagePath(), uploadProperties.getActesMariageDir());
        register(registry, uploadProperties.getPublicAttestationsPath(), uploadProperties.getAttestationsDir());
        register(registry, uploadProperties.getPublicDocumentsFormationsPath(), uploadProperties.getDocumentsFormationsDir());
        register(registry, uploadProperties.getPublicDocumentJustificatifPath(), uploadProperties.getDocumentJustificatifDir());
        register(registry, uploadProperties.getPublicDossiersAgentsPath(), uploadProperties.getDossiersAgentsDir());
        register(registry, uploadProperties.getPublicSpecimenPath(), uploadProperties.getSpecimenDir());
    }

    private void register(ResourceHandlerRegistry registry, String publicPath, String storageDir) {
        Path dir = Paths.get(storageDir).toAbsolutePath().normalize();
        registry.addResourceHandler("/public/" + publicPath + "/**")
                .addResourceLocations("file:" + dir + "/");
    }
}
