package com.example.gestion_utilisateur.Controller;

import com.example.gestion_utilisateur.Entity.Utilisateur;
import com.example.gestion_utilisateur.Service.UtilisateurService;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * Appelé par le frontend juste après une authentification Keycloak réussie
     * pour récupérer (ou créer à la volée) l'utilisateur local correspondant.
     *
     * Body attendu : {
     *   "keycloakSub": "<uuid>",
     *   "email":       "...",
     *   "nom":         "...",
     *   "prenom":      "...",
     *   "roles":       ["USER"]  ou ["ADMIN","USER"]
     * }
     */
    /**
     * Inscription : crée le user dans Keycloak (Admin API) + en base locale.
     * Body attendu : { nom, prenom, email, motDePasse, telephone? }
     * Cette route est publique (pas de Bearer requis).
     */
    @PostMapping("/register-keycloak")
    public Utilisateur registerKeycloak(@RequestBody Map<String, Object> payload) {
        String email      = asString(payload.get("email"));
        String motDePasse = asString(payload.get("motDePasse"));
        String nom        = asString(payload.get("nom"));
        String prenom     = asString(payload.get("prenom"));
        String telephone  = asString(payload.get("telephone"));

        if (email == null || motDePasse == null || nom == null || prenom == null) {
            throw new IllegalArgumentException("email, motDePasse, nom et prenom sont requis");
        }
        return utilisateurService.inscrireAvecKeycloak(email, motDePasse, nom, prenom, telephone);
    }

    @PostMapping("/sync-keycloak")
    public Utilisateur synchroniserKeycloak(@RequestBody Map<String, Object> payload) {
        String sub    = asString(payload.get("keycloakSub"));
        String email  = asString(payload.get("email"));
        String nom    = asString(payload.get("nom"));
        String prenom = asString(payload.get("prenom"));

        Set<String> roles = new HashSet<>();
        Object rolesObj = payload.get("roles");
        if (rolesObj instanceof Iterable<?> iterable) {
            for (Object r : iterable) {
                if (r != null) roles.add(r.toString());
            }
        }

        if (sub == null || email == null) {
            throw new IllegalArgumentException("keycloakSub et email sont requis");
        }
        return utilisateurService.synchroniserDepuisKeycloak(sub, email, nom, prenom, roles);
    }

    private String asString(Object o) {
        return o == null ? null : o.toString();
    }
}