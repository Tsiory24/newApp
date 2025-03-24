package com.example.newApp.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.example.newApp.model.Client;
import com.example.newApp.model.Invoice;
import com.example.newApp.model.InvoiceLine;
import com.example.newApp.util.Util;

@Controller
@RequestMapping("/admin")
public class InvoiceController {
    private final String apiUrl = "http://localhost/api/invoices";

    @GetMapping("/invoices")
public String showInvoices(Model model) {
    RestTemplate restTemplate = new RestTemplate();
    List<Invoice> invoices = new ArrayList<>();

    // Définition du format de date utilisé par l'API
    try {
        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody != null) {
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data != null) {
                List<Map<String, Object>> invoicesRaw = (List<Map<String, Object>>) data.get("invoices");

                // Construction manuelle des objets Invoice avec les setters
                for (Map<String, Object> invoiceMap : invoicesRaw) {
                    Invoice invoice = new Invoice();
                    invoice.setId((Integer) invoiceMap.get("id"));
                    invoice.setExternalId((String) invoiceMap.get("external_id"));
                    invoice.setStatus((String) invoiceMap.get("status"));
                    invoice.setInvoiceNumber((Integer) invoiceMap.get("invoice_number"));

                    // Conversion des dates en LocalDateTime
                    invoice.setSentAt(Util.parseSentAtDate((String) invoiceMap.get("sent_at")));
                    invoice.setDueAt(Util.parseDate((String)invoiceMap.get("due_at")));

                    // Client mapping
                    Client client = new Client();
                    client.setId((Integer) invoiceMap.get("client_id"));
                    client.setCompanyName((String) invoiceMap.get("company_name"));
                    invoice.setClient(client);
                    invoice.setOfferId((int)invoiceMap.get("offer_id"));
                    invoice.setCreatedAt(Util.parseSentAtDate((String) invoiceMap.get("created_at")));

                    // Setting total amount as BigDecimal
                    invoice.setTotalAmount(Double.parseDouble((String) invoiceMap.get("total_amount")));

                    invoices.add(invoice);
                }
            }
        }
    } catch (Exception e) {
        model.addAttribute("error", "Erreur lors de la récupération des factures !");
        e.printStackTrace();
    }

    // Ajouter la liste des factures à la vue
    model.addAttribute("invoices", invoices);
    return "admin/pages/invoices";
}

    @GetMapping("/detailsSommePrixInvoices")
    public String showDetailsSommePrixInvoices(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        List<InvoiceLine> invoiceLines = new ArrayList<>();
        
        // Format de date utilisé par l'API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        try {
            // Récupérer les données de l'API
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl + "/detailsSommePrixInvoices", Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody != null) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                if (data != null) {
                    List<Map<String, Object>> detailsRaw = (List<Map<String, Object>>) data.get("detailsSommePrixInvoices");
                    
                    // Construction des objets InvoiceLine
                    for (Map<String, Object> invoiceLineMap : detailsRaw) {
                        InvoiceLine invoiceLine = new InvoiceLine();
                        
                        invoiceLine.setId((Integer) invoiceLineMap.get("id"));
                        invoiceLine.setExternalId((String) invoiceLineMap.get("external_id"));
                        invoiceLine.setTitle((String) invoiceLineMap.get("title"));
                        invoiceLine.setComment((String) invoiceLineMap.get("comment"));
                        // invoiceLine.setPrice((Number) invoiceLineMap.get("price"));
                        Object priceObj = invoiceLineMap.get("price");
                        if (priceObj instanceof Integer) {
                            invoiceLine.setPrice(((Integer) priceObj).doubleValue()); // Convertir en Double si c'est un Integer
                        } else if (priceObj instanceof Double) {
                            invoiceLine.setPrice((Double) priceObj); // Si c'est déjà un Double
                        }
                        invoiceLine.setInvoiceId((Integer) invoiceLineMap.get("invoice_id"));
                        invoiceLine.setOfferId((Integer) invoiceLineMap.get("offer_id"));
                        invoiceLine.setType((String) invoiceLineMap.get("type"));
                        invoiceLine.setQuantity((Integer) invoiceLineMap.get("quantity"));
                        invoiceLine.setProductId((Integer) invoiceLineMap.get("product_id"));
                        invoiceLine.setCreatedAt(Util.parseStringWithZTodate((String)invoiceLineMap.get("created_at")));
                        invoiceLine.setUpdatedAt(Util.parseStringWithZTodate((String)invoiceLineMap.get("updated_at")));
                        invoiceLine.setDeletedAt(Util.parseStringWithZTodate((String)invoiceLineMap.get("deleted_at")));
                        
                        // Pour l'objet "invoice" imbriqué
                        Map<String, Object> invoiceMap = (Map<String, Object>) invoiceLineMap.get("invoice");
                        if (invoiceMap != null) {
                            Invoice invoice = new Invoice();
                            invoice.setId((Integer) invoiceMap.get("id"));
                            invoice.setExternalId((String) invoiceMap.get("external_id"));
                            invoice.setStatus((String) invoiceMap.get("status"));
                            invoice.setInvoiceNumber((Integer) invoiceMap.get("invoice_number"));
                            invoice.setSentAt(Util.parseSentAtDate((String)invoiceMap.get("sent_at")));
                            invoice.setDueAt(Util.parseStringWithZTodate((String)invoiceMap.get("due_at")));
                            // invoice.setClientId((Integer) invoiceMap.get("client_id"));                            
                            
                            invoiceLine.setInvoice(invoice); // Affectation de l'objet Invoice à InvoiceLine
                        }

                        invoiceLines.add(invoiceLine);
                    }
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des lignes de factures !");
            e.printStackTrace();
        }
        
        // Ajouter la liste des lignes de factures à la vue
        model.addAttribute("invoiceLines", invoiceLines);
        return "admin/pages/invoicelines"; // Retourner la vue de factures
    }
}
