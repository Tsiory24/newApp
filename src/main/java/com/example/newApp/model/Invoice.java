package com.example.newApp.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    int id;
    String externalId;
    String status;
    int invoiceNumber;
    LocalDateTime sentAt;
    LocalDateTime createdAt;
    LocalDateTime dueAt;
    Client client;
    double totalAmount;
    int offerId;
}
