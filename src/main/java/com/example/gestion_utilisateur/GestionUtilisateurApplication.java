package com.example.gestion_utilisateur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients

public class GestionUtilisateurApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionUtilisateurApplication.class, args);
    }

}
