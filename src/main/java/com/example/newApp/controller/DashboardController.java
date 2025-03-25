package com.example.newApp.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.newApp.model.Client;
import com.example.newApp.model.Invoice;
import com.example.newApp.model.InvoiceLine;
import com.example.newApp.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.newApp.util.Util;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/admin")
@Controller
public class DashboardController {
    private final String apiUrl = "http://localhost/api/dashboard";

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "year", required = false) Integer year, Model model, RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();

        // Si l'année n'est pas fournie, définir 2025 comme valeur par défaut
        if (year == null) {
            year = 2025; // Année par défaut si 'year' est null
        }

        System.out.println("Year: " + year);

        // Créer l'entité de la requête avec l'année dans l'URL
        Map<String, Integer> requestData = new HashMap<>();
        requestData.put("year", year);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Integer>> requestEntity = new HttpEntity<>(requestData, headers);

        try {
            // Construire l'URL avec l'année comme paramètre de requête
            String urlWithYear = apiUrl + "?year=" + year;

            // Envoi de la requête GET vers l'API Laravel pour récupérer les données du tableau de bord
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    urlWithYear, HttpMethod.GET, requestEntity, Map.class
            );

            Map<String, Object> responseBody = responseEntity.getBody();

            if (responseBody != null) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

                if (data != null) {
                    model.addAttribute("offersCount", data.get("offers_count"));
                    model.addAttribute("invoicesCount", data.get("invoices_count"));
                    model.addAttribute("totalAmountInvoice", data.get("total_amount_invoice"));
                    model.addAttribute("sumPayment", data.get("sum_payment"));
                    model.addAttribute("sumPaymentDue", Math.abs((int) data.get("sum_payment_due")));
                    model.addAttribute("paymentMensuelles", data.get("payment_mensuelles"));
                    model.addAttribute("nombreParTypePayment", data.get("nombre_par_type_payment"));
                    model.addAttribute("paymentParAn", data.get("payment_par_an"));
                    model.addAttribute("invoicelineParAn", data.get("invoiceline_par_an"));
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la récupération des données !");
            e.printStackTrace();
        }

        return "admin/pages/dashboard"; // Nom de la vue
    }



    @GetMapping("/dashboard/aPayer")
    public String showAPayer(Model model) {
        return "admin/pages/aPayer"; // Vue pour "A payer"
    }

    @GetMapping("/dashboard/clients")
    public String showClients(Model model) {
        return "admin/pages/clients"; // Vue pour les clients
    }

    
}
