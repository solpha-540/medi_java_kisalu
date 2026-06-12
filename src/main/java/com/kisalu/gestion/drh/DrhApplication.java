package com.kisalu.gestion.drh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API DRH Kisalu - Spring Boot
 * Portage de l'architecture PHP Slim 4 (app_api/app) vers Spring Boot 3.
 *
 * Structure :
 * - model/      : entites JPA (92 tables kisalu_sandBox)
 * - repository/ : acces donnees (equivalent *.serializer.php)
 * - service/    : logique metier
 * - controller/ : routes REST /api/v1/{module}/...
 * - common/     : JWT, ApiResponse, gestion erreurs
 */
@SpringBootApplication
public class DrhApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrhApplication.class, args);
	}

}
