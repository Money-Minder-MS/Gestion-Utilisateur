package com.example.gestion_utilisateur.Service;

import com.example.gestion_utilisateur.Entity.Role;
import com.example.gestion_utilisateur.Entity.Utilisateur;
import com.example.gestion_utilisateur.Repository.RoleRepository;
import com.example.gestion_utilisateur.Repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final KeycloakAdminClient keycloakAdminClient;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              RoleRepository roleRepository,
                              KeycloakAdminClient keycloakAdminClient) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.keycloakAdminClient = keycloakAdminClient;
    }

    /**
     * Inscrit un nouvel utilisateur :
     *   1. Crée le compte dans Keycloak (avec mot de passe) via l'Admin API
     *   2. Persiste l'utilisateur en base locale (sans le mot de passe)
     *   3. Retourne l'entité locale, le frontend peut alors se loger via direct grants
     */
    public Utilisateur inscrireAvecKeycloak(String email, String motDePasse,
                                            String nom, String prenom,
                                            String telephone) {
        if (utilisateurRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalStateException("Un compte existe déjà avec cet email.");
        }

        String keycloakSub = keycloakAdminClient.createUser(email, prenom, nom, motDePasse);

        Utilisateur u = new Utilisateur();
        u.setEmail(email);
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setTelephone(telephone);
        u.setKeycloakSub(keycloakSub);
        u.setRoleId(resoudreRoleId(java.util.Set.of("USER")));
        return utilisateurRepository.save(u);
    }

    public Utilisateur ajouter(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> afficherTout() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur afficherParId(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    public Utilisateur modifier(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public void supprimer(Long id) {
        utilisateurRepository.deleteById(id);
    }

    /**
     * Synchronise un utilisateur Keycloak avec la base locale.
     *
     * <ul>
     *   <li>Si un utilisateur existe déjà avec ce {@code keycloakSub}, on le retourne.</li>
     *   <li>Sinon si un utilisateur existe avec cet email (legacy), on lui attache le sub Keycloak.</li>
     *   <li>Sinon on crée un nouvel utilisateur avec un rôle dérivé des rôles Keycloak.</li>
     * </ul>
     *
     * Le rôle local est résolu ainsi : si "ADMIN" figure dans {@code keycloakRoles}, on
     * utilise le rôle local dont le nom contient "ADMIN" ; sinon le rôle dont le nom
     * contient "CLIENT" (ou "USER" en fallback).
     */
    public Utilisateur synchroniserDepuisKeycloak(String keycloakSub, String email,
                                                  String nom, String prenom,
                                                  Set<String> keycloakRoles) {
        Optional<Utilisateur> bySub = utilisateurRepository.findByKeycloakSub(keycloakSub);
        if (bySub.isPresent()) {
            return bySub.get();
        }

        Optional<Utilisateur> byEmail = utilisateurRepository.findByEmailIgnoreCase(email);
        if (byEmail.isPresent()) {
            Utilisateur u = byEmail.get();
            u.setKeycloakSub(keycloakSub);
            return utilisateurRepository.save(u);
        }

        Utilisateur u = new Utilisateur();
        u.setEmail(email);
        u.setKeycloakSub(keycloakSub);
        u.setNom(nom != null && !nom.isBlank() ? nom : "Utilisateur");
        u.setPrenom(prenom != null && !prenom.isBlank() ? prenom : "");
        u.setRoleId(resoudreRoleId(keycloakRoles));
        return utilisateurRepository.save(u);
    }

    private Long resoudreRoleId(Set<String> keycloakRoles) {
        List<Role> roles = roleRepository.findAll();
        boolean isAdmin = keycloakRoles != null && keycloakRoles.stream()
                .anyMatch(r -> r != null && r.toUpperCase().contains("ADMIN"));

        return roles.stream()
                .filter(r -> r.getNom() != null && (isAdmin
                        ? r.getNom().toUpperCase().contains("ADMIN")
                        : r.getNom().toUpperCase().contains("CLIENT") || r.getNom().toUpperCase().contains("USER")))
                .map(Role::getId)
                .findFirst()
                .orElseGet(() -> roles.isEmpty() ? null : roles.get(0).getId());
    }
}