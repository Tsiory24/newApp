package com.example.newApp.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.example.newApp.model.Client;
import com.example.newApp.model.Invoice;
import com.example.newApp.model.Offer;
import com.example.newApp.util.Util;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class OfferController {
    private final String apiUrl = "http://localhost/api/offers";
    
    @GetMapping("/offers")
    public String showOffers(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
    
            // Récupérer le corps de la réponse
            Map<String, Object> responseBody = response.getBody();
            List<Offer> offers = new ArrayList<>();
            // Vérifier si la réponse est valide
            if (responseBody != null) {
                // Extraire le champ "data" contenant les informations nécessaires
                Map<String, Object> responseData = (Map<String, Object>) responseBody.get("data");
    
                if (responseData != null) {
                    // Passer les données extraites du JSON à la vue via le modèle
                    List<Map<String,Object>> data = (List<Map<String,Object>>)responseData.get("offers");
                    if(data !=null){
                        for (Map<String, Object> offerMap : data) {
                            Offer offer = new Offer();
                            offer.setId((int) offerMap.get("id"));
                            offer.setExternalId((String) offerMap.get("external_id"));
                            offer.setStatus((String) offerMap.get("status"));
                            Client client = new Client();
                            if(offerMap.get("client")!=null){
                                Map<String, Object> clientMap = (Map<String, Object>) offerMap.get("client");
                                client.setId((int) clientMap.get("id"));
                                client.setCompanyName((String) clientMap.get("company_name"));
                            }
                            offer.setClient(client);
                            LocalDateTime createdAt = Util.parseStringWithZTodate((String)offerMap.get("created_at"));
                            LocalDateTime updatedAt = Util.parseStringWithZTodate((String)offerMap.get("updated_at"));
                            offer.setCreatedAt(createdAt);
                            offer.setUpdatedAt(updatedAt);
                            offers.add(offer);
                        }
                    }
                }
            }
            model.addAttribute("offers", offers);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des données !"+e.getMessage());
            e.printStackTrace();
        }
        return "admin/pages/offers"; // Vue pour les offres
    }
}
