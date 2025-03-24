package com.example.newApp.model;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private int id;
    private String externalId;
    private double amount;
    private String description;
    private String paymentSource;
    private LocalDateTime paymentDate;
    private String integrationPaymentId;
    private String integrationType;
    private Invoice invoice;  // Relation avec l'objet Invoice
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
