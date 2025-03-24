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
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl+"/offers", Map.class);
    
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
    RestTemplate restTemplate = new RestTemplate();
    List<Invoice> invoices = new ArrayList<>();

    // Définition du format de date utilisé par l'API
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    try {
        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl + "/invoices", Map.class);
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
                    invoice.setDueAt(Util.parseDate(invoiceMap.get("due_at"), formatter));

                    // Client mapping
                    Client client = new Client();
                    client.setId((Integer) invoiceMap.get("client_id"));
                    client.setCompanyName((String) invoiceMap.get("company_name"));
                    invoice.setClient(client);

                    // Setting total amount as BigDecimal
                    invoice.setTotalAmount(new BigDecimal((String) invoiceMap.get("total_amount")));

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


    

    @GetMapping("/dashboard/detailsSommePrixInvoices")
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
                        invoiceLine.setCreatedAt(Util.parseDate(invoiceLineMap.get("created_at"), formatter));
                        invoiceLine.setUpdatedAt(Util.parseDate(invoiceLineMap.get("updated_at"), formatter));
                        invoiceLine.setDeletedAt(Util.parseDate(invoiceLineMap.get("deleted_at"), formatter));
                        
                        // Pour l'objet "invoice" imbriqué
                        Map<String, Object> invoiceMap = (Map<String, Object>) invoiceLineMap.get("invoice");
                        if (invoiceMap != null) {
                            Invoice invoice = new Invoice();
                            invoice.setId((Integer) invoiceMap.get("id"));
                            invoice.setExternalId((String) invoiceMap.get("external_id"));
                            invoice.setStatus((String) invoiceMap.get("status"));
                            invoice.setInvoiceNumber((Integer) invoiceMap.get("invoice_number"));
                            invoice.setSentAt(Util.parseDate(invoiceMap.get("sent_at"), formatter));
                            invoice.setDueAt(Util.parseDate(invoiceMap.get("due_at"), formatter));
                            invoice.setIntegrationInvoiceId((String) invoiceMap.get("integration_invoice_id"));
                            invoice.setIntegrationType((String) invoiceMap.get("integration_type"));
                            invoice.setSourceType((String) invoiceMap.get("source_type"));
                            invoice.setSourceId((Integer) invoiceMap.get("source_id"));
                            // invoice.setClientId((Integer) invoiceMap.get("client_id"));
                            invoice.setOfferId((Integer) invoiceMap.get("offer_id"));
                            invoice.setDeletedAt(Util.parseDate(invoiceMap.get("deleted_at"), formatter));
                            invoice.setCreatedAt(Util.parseDate(invoiceMap.get("created_at"), formatter));
                            invoice.setUpdatedAt(Util.parseDate(invoiceMap.get("updated_at"), formatter));
                            
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
    @GetMapping("/dashboard/payments")
   public String showPayments(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        List<Payment> payments = new ArrayList<>();

        // Définir le format de date utilisé par l'API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                        payment.setPaymentDate(Util.parseDate((String) paymentMap.get("payment_date"), formatter));
                        payment.setCreatedAt(Util.parseDate((String) paymentMap.get("created_at"), formatter));
                        payment.setUpdatedAt(Util.parseDate((String) paymentMap.get("updated_at"), formatter));
                        payment.setDeletedAt(Util.parseDate((String) paymentMap.get("deleted_at"), formatter));

                        // Gestion de l'Invoice
                        Map<String, Object> invoiceMap = (Map<String, Object>) paymentMap.get("invoice");
                        if (invoiceMap != null) {
                            Invoice invoice = new Invoice();
                            invoice.setId((Integer) invoiceMap.get("id"));
                            invoice.setExternalId((String) invoiceMap.get("external_id"));
                            invoice.setStatus((String) invoiceMap.get("status"));
                            invoice.setInvoiceNumber((Integer) invoiceMap.get("invoice_number"));
                            invoice.setSentAt(Util.parseDate((String) invoiceMap.get("sent_at"), formatter));
                            invoice.setDueAt(Util.parseDate((String) invoiceMap.get("due_at"), formatter));
                            invoice.setIntegrationInvoiceId((String) invoiceMap.get("integration_invoice_id"));
                            invoice.setIntegrationType((String) invoiceMap.get("integration_type"));
                            invoice.setSourceType((String) invoiceMap.get("source_type"));
                            invoice.setSourceId((Integer) invoiceMap.get("source_id"));
                            invoice.setOfferId((Integer) invoiceMap.get("offer_id"));
                            invoice.setDeletedAt(Util.parseDate((String) invoiceMap.get("deleted_at"), formatter));
                            invoice.setCreatedAt(Util.parseDate((String) invoiceMap.get("created_at"), formatter));
                            invoice.setUpdatedAt(Util.parseDate((String) invoiceMap.get("updated_at"), formatter));

                            // Associer l'Invoice au Payment
                            payment.setInvoice(invoice);
                        }

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


    @GetMapping("/dashboard/clients")
    public String showClients(Model model) {
        return "admin/pages/clients"; // Vue pour les clients
    }

    
}
