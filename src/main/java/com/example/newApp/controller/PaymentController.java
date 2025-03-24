package com.example.newApp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.newApp.model.Invoice;
import com.example.newApp.model.Payment;
import com.example.newApp.util.ResponseUtil;
import com.example.newApp.util.Util;

import org.springframework.http.*;


@Controller
@RequestMapping("/admin")
public class PaymentController {

    private final String apiUrl = "http://localhost/api/payment";

    @GetMapping("/payments")
   public String showPayments(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        List<Payment> payments = new ArrayList<>();

        // Définir le format de date utilisé par l'API
        

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl + "/payments", Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                if (data != null) {
                    List<Map<String, Object>> paymentsRaw = (List<Map<String, Object>>) data.get("payements");

                    // Construction des objets Payment
                    for (Map<String, Object> paymentMap : paymentsRaw) {
                        Payment payment = new Payment();
                        payment.setId((Integer) paymentMap.get("id"));
                        payment.setExternalId((String) paymentMap.get("external_id"));
                        // payment.setAmount((Double) paymentMap.get("amount"));
                        Object amountObj = paymentMap.get("amount");
                        if (amountObj instanceof Integer) {
                            payment.setAmount(((Integer) amountObj).doubleValue());
                        } else if (amountObj instanceof Double) {
                            payment.setAmount((Double) amountObj);
                        } else {
                            // Gérer un cas inattendu, par exemple en mettant une valeur par défaut
                            payment.setAmount(0.0);
                        }

                        payment.setDescription((String) paymentMap.get("description"));
                        payment.setPaymentSource((String) paymentMap.get("payment_source"));

                        // Parsing des dates
                        payment.setPaymentDate(Util.parseStringWithZTodate((String) paymentMap.get("payment_date")));
                        payment.setCreatedAt(Util.parseStringWithZTodate((String) paymentMap.get("created_at")));
                        payment.setUpdatedAt(Util.parseStringWithZTodate((String) paymentMap.get("updated_at")));
                        payment.setDeletedAt(Util.parseStringWithZTodate((String) paymentMap.get("deleted_at")));

                        // Gestion de l'Invoice
                        int invoiceId =  (int)paymentMap.get("invoice_id");
                        payment.setInvoiceId(invoiceId);
                       

                        payments.add(payment);
                    }
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des paiements !");
            e.printStackTrace();
        }

        // Ajouter la liste des paiements à la vue
        model.addAttribute("payments", payments);
        return "admin/pages/payments";
    }

    @PostMapping("/payment/edit")
public String updatePayment(@RequestParam String externalId, @RequestParam double amount, RedirectAttributes redirectAttributes) {
    // Logique de mise à jour
    RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> requestData = new HashMap<>();
    System.out.println("ExternalId: " + externalId); // Vérifiez que cette valeur est bien passée
    System.out.println("Amount: " + amount);
    requestData.put("externalId", externalId);
    requestData.put("amount", amount);

    // Construire la requête HTTP
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
            redirectAttributes.addFlashAttribute("message", "Paiement mis à jour avec succès !");
        } else {
            String errorMessage = (String) responseBody.get("error");
            redirectAttributes.addFlashAttribute("error", "Erreur : " + errorMessage);
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la communication avec l'API.");
        e.printStackTrace();
    }

    // Rediriger vers la page des paiements après la mise à jour
    return "redirect:/admin/payments";
}

@PostMapping("/payment/delete")
public String deletePayment(@RequestParam String externalId,RedirectAttributes redirectAttributes) {
    // Logique de mise à jour
    RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> requestData = new HashMap<>();
    System.out.println("ExternalId: " + externalId); // Vérifiez que cette valeur est bien passée
    requestData.put("externalId", externalId);
    // Construire la requête HTTP
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);

    try {
        // Envoi de la requête POST vers l'API externe
        ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl + "/delete", HttpMethod.POST, requestEntity, Map.class);

        // Lire la réponse de l'API
        Map<String, Object> responseBody = responseEntity.getBody();
        String status = (String) responseBody.getOrDefault("status", "error");

        if ("success".equals(status)) {
            redirectAttributes.addFlashAttribute("message", "Paiement supprimer avec succes");
        } else {
            String errorMessage = (String) responseBody.get("error");
            redirectAttributes.addFlashAttribute("error", "Erreur : " + errorMessage);
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la communication avec l'API.");
        e.printStackTrace();
    }

    // Rediriger vers la page des paiements après la mise à jour
    return "redirect:/admin/payments";
}


}
