package com.example.newApp.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/admin")
@Controller
public class DashboardController {
    private final String apiUrl = "http://localhost/api/dashboard";

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        RestTemplate restTemplate = new RestTemplate();    
        try {
            // Appel de l'API Laravel pour récupérer les données du tableau de bord
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
    
            // Récupérer le corps de la réponse
            Map<String, Object> responseBody = response.getBody();
    
            // Vérifier si la réponse est valide
            if (responseBody != null) {
                // Extraire le champ "data" contenant les informations nécessaires
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
    
                if (data != null) {
                    // Passer les données extraites du JSON à la vue via le modèle
                    model.addAttribute("offersCount", data.get("offers_count"));
                    model.addAttribute("invoicesCount", data.get("invoices_count"));
                    model.addAttribute("totalAmountInvoice", data.get("total_amount_invoice"));
                    model.addAttribute("sumPayment", data.get("sum_payment"));
                    model.addAttribute("sumPaymentDue", Math.abs((int)data.get("sum_payment_due"))); // Assurez-vous de gérer l'absolu si nécessaire
                    model.addAttribute("paymentMensuelles", data.get("payment_mensuelles"));
                    model.addAttribute("nombreParTypePayment", data.get("nombre_par_type_payment"));
                    model.addAttribute("paymentParAn", data.get("payment_par_an"));
                    model.addAttribute("invoicelineParAn", data.get("invoiceline_par_an"));
                }
            }
    
        } catch (Exception e) {
            // Gérer l'erreur si l'appel API échoue
            model.addAttribute("error", "Erreur lors de la récupération des données !");
            e.printStackTrace();
        }
    
        // Retourner le nom de la vue (HTML ou JSP)
        return "admin/pages/dashboard";
    }

    @GetMapping("/dashboard/offers")
    public String showOffers(Model model) {
        RestTemplate restTemplate = new RestTemplate();   
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
    
            // Récupérer le corps de la réponse
            Map<String, Object> responseBody = response.getBody();
    
            // Vérifier si la réponse est valide
            if (responseBody != null) {
                // Extraire le champ "data" contenant les informations nécessaires
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
    
                if (data != null) {
                    // Passer les données extraites du JSON à la vue via le modèle
                    model.addAttribute("offers", data.get("offers"));
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des données !");
            e.printStackTrace();
        }
        return "admin/pages/offers"; // Vue pour les offres
    }

    @GetMapping("/dashboard/aPayer")
    public String showAPayer(Model model) {
        return "admin/pages/aPayer"; // Vue pour "A payer"
    }

    @GetMapping("/dashboard/invoices")
    public String showInvoices(Model model) {
        return "admin/pages/invoices"; // Vue pour les factures
    }

    @GetMapping("/dashboard/detailsSommePrixInvoices")
    public String showDetailsSommePrixInvoices(Model model) {
        return "admin/pages/detailsSommePrixInvoices"; // Vue pour les détails des factures
    }

    @GetMapping("/dashboard/payments")
    public String showPayments(Model model) {
        return "admin/pages/payments"; // Vue pour les paiements
    }

    @GetMapping("/dashboard/clients")
    public String showClients(Model model) {
        return "admin/pages/clients"; // Vue pour les clients
    }
}
