package com.example.newApp.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    private int id;
    private String externalId;
    private String status;
    private int invoiceNumber;
    private LocalDateTime sentAt;
    private LocalDateTime dueAt;

    private String integrationInvoiceId;
    private String integrationType;
    private String sourceType;
    private int sourceId;
    private int offerId;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Client client; // Relation avec Client
    private BigDecimal totalAmount; // Ajout du montant total
}
