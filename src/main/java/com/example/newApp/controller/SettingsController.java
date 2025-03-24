package com.example.newApp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@RequestMapping("/admin")
public class SettingsController {
    private final String apiUrl = "http://localhost/api/settings";

    @GetMapping("/remise")
    public String getRemise(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl + "/remise", Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                if (data != null) {
                    Map<String, Object> remise = (Map<String, Object>) data.get("remise");
                    model.addAttribute("remise", Double.parseDouble((String)remise.get("remise")));
                }
            }
        }catch(Exception e){
            model.addAttribute("error", "Erreur lors de la récupération de remise !");
            e.printStackTrace();
        }
        return "admin/pages/configurationRemise";

    }
    
    @PostMapping("/settings/update")
    public String postMethodName(@RequestParam double remise,RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestData = new HashMap<>();
    
        requestData.put("remise", remise);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);

        try {
            // Envoi de la requête POST vers l'API externe
            ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl + "/update", HttpMethod.POST, requestEntity, Map.class);

            // Lire la réponse de l'API
            Map<String, Object> responseBody = responseEntity.getBody();
            String status = (String) responseBody.getOrDefault("status", "error");

            if ("success".equals(status)) {
                redirectAttributes.addFlashAttribute("message", (String)responseBody.get("message"));
            } else {
                String errorMessage = (String) responseBody.get("error");
                redirectAttributes.addFlashAttribute("error", "Erreur : " + errorMessage);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la modification de remise.");
            e.printStackTrace();
        }
        return "redirect:/admin/remise";
    }
    
}
