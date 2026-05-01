package com.example.gestion_utilisateur.Service;

import com.example.gestion_utilisateur.Entity.Utilisateur;
import com.example.gestion_utilisateur.Repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
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
}