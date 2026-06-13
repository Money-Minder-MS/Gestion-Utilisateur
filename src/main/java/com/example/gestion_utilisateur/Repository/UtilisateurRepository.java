package com.example.gestion_utilisateur.Repository;

import com.example.gestion_utilisateur.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByKeycloakSub(String keycloakSub);
    Optional<Utilisateur> findByEmailIgnoreCase(String email);
}