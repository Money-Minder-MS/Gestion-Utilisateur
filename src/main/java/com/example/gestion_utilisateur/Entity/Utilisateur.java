package com.example.gestion_utilisateur.Entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String nom;

    private String prenom;

    private LocalDate dateNaissance;

    private String telephone;

    private String email;

    private String motDePasse;

    @Column(name = "keycloak_sub", unique = true)
    private String keycloakSub;

    @Column(name = "role_id")
    private Long roleId;

    public Utilisateur() {
    }

    public Utilisateur(Long id, String nom, String prenom, LocalDate dateNaissance,
                       String telephone, String email, String motDePasse, Long roleId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.email = email;
        this.motDePasse = motDePasse;
        this.roleId = roleId;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public String getKeycloakSub() { return keycloakSub; }
    public Long getRoleId() { return roleId; }

    public void setId(Long id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setEmail(String email) { this.email = email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setKeycloakSub(String keycloakSub) { this.keycloakSub = keycloakSub; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}