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
            // Appel de l'API Laravel
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data"); // Extraction du champ "data"
                model.addAttribute("offersCount", data.get("offers_count"));
                model.addAttribute("invoicesCount", data.get("invoices_count"));
                model.addAttribute("totalAmountInvoice", data.get("total_amount_invoice"));
                model.addAttribute("sumPayment", data.get("sum_payment"));
                model.addAttribute("sumPaymentDue", Math.abs((int)data.get("sum_payment_due")));
            }

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des données !");
        }

        return "admin/pages/dashboard";
    }

    @GetMapping("/dashboard/offers")
    public String showOffers(Model model) {
        return "redirect:/admin/dashboard"; // Vue pour les offres
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
