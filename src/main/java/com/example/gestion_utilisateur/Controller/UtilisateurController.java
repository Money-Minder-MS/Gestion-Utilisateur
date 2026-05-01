package com.example.gestion_utilisateur.Controller;

import com.example.gestion_utilisateur.Entity.Utilisateur;
import com.example.gestion_utilisateur.Service.UtilisateurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/add")
    public Utilisateur ajouter(@RequestBody Utilisateur utilisateur) {
        return utilisateurService.ajouter(utilisateur);
    }

    @GetMapping("/getall")
    public List<Utilisateur> afficherTout() {
        return utilisateurService.afficherTout();
    }

    @GetMapping("/getbyid/{id}")
    public Utilisateur afficherParId(@PathVariable Long id) {
        return utilisateurService.afficherParId(id);
    }

    @PutMapping("/update")
    public Utilisateur modifier(@RequestBody Utilisateur utilisateur) {
        return utilisateurService.modifier(utilisateur);
    }

    @DeleteMapping("/delete/{id}")
    public void supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
    }
}