package com.example.gestion_utilisateur.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Client minimal pour l'API Admin de Keycloak.
 * Permet de créer un utilisateur dans le realm + lui assigner le rôle USER.
 */
@Service
public class KeycloakAdminClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${keycloak.url}")
    private String url;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.default-role}")
    private String defaultRole;

    /** Récupère un token admin (master realm). */
    private String getAdminToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", adminUsername);
        form.add("password", adminPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> resp = rest.postForEntity(
                url + "/realms/master/protocol/openid-connect/token",
                new HttpEntity<>(form, headers),
                Map.class
        );
        return (String) resp.getBody().get("access_token");
    }

    /**
     * Crée un user dans le realm et lui assigne le rôle par défaut.
     * @return l'UUID Keycloak (sub) du user créé
     */
    public String createUser(String email, String prenom, String nom, String motDePasse) {
        String token = getAdminToken();

        // 1. Créer le user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> credential = Map.of(
                "type", "password",
                "value", motDePasse,
                "temporary", false
        );
        Map<String, Object> payload = Map.of(
                "username", email,
                "email", email,
                "firstName", prenom == null ? "" : prenom,
                "lastName", nom == null ? "" : nom,
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(credential)
        );

        ResponseEntity<Void> createResp;
        try {
            createResp = rest.exchange(
                    url + "/admin/realms/" + realm + "/users",
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    Void.class
            );
        } catch (HttpClientErrorException.Conflict e) {
            throw new IllegalStateException("Un compte existe déjà avec cet email.", e);
        }

        // L'API Keycloak retourne 201 Created avec Location: /admin/realms/.../users/<sub>
        String location = createResp.getHeaders().getFirst(HttpHeaders.LOCATION);
        if (location == null) {
            throw new IllegalStateException("Keycloak n'a pas renvoyé l'URL du user créé.");
        }
        String userId = location.substring(location.lastIndexOf('/') + 1);

        // 2. Assigner le rôle realm
        try {
            assignRealmRole(token, userId, defaultRole);
        } catch (Exception e) {
            // Le user est créé, on ne rollback pas mais on log
            System.err.println("[Keycloak] Impossible d'assigner le rôle '" + defaultRole + "' : " + e.getMessage());
        }

        return userId;
    }

    private void assignRealmRole(String token, String userId, String roleName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // Récupérer la représentation du rôle
        ResponseEntity<Map> roleResp = rest.exchange(
                url + "/admin/realms/" + realm + "/roles/" + roleName,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );
        Map<String, Object> role = roleResp.getBody();
        if (role == null) {
            throw new IllegalStateException("Rôle realm introuvable : " + roleName);
        }

        // POST sur role-mappings/realm avec la liste contenant ce rôle
        headers.setContentType(MediaType.APPLICATION_JSON);
        rest.exchange(
                url + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm",
                HttpMethod.POST,
                new HttpEntity<>(List.of(role), headers),
                Void.class
        );
    }
}
