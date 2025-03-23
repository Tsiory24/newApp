package com.example.newApp.service;

import com.example.newApp.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthentificationService {
    private final RestTemplate restTemplate;

    @Autowired
    public AuthentificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseUtil login(String url, String email, String password) {
        // Création du corps de la requête
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        // Configuration des headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construction de la requête HTTP
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            // Appel de l'API
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, Map.class
            );

            // Vérification du statut de la réponse
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                String status = (String) responseBody.get("status");

                if ("success".equals(status)) {
                    return new ResponseUtil("success", (Map<String, Object>) responseBody.get("data"), null);
                } else {
                    return new ResponseUtil("error", null, (String) responseBody.get("error"));
                }
            }
        } catch (Exception e) {
            return new ResponseUtil("error", null, "Erreur lors de la connexion à l'API : " + e.getMessage());
        }

        return new ResponseUtil("error", null, "Réponse invalide");
    }
}
