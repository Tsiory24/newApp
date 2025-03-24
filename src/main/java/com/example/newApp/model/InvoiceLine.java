package com.example.newApp.model;

import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class InvoiceLine {

    private Integer id;
    private String externalId;
    private String title;
    private String comment;
    private Double price;
    private Integer invoiceId;
    private Integer offerId;
    private String type;
    private Integer quantity;
    private Integer productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Invoice invoice; // Contient l'objet "Invoice" imbriqué
}