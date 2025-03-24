package com.example.newApp.model;
import java.time.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Offer {
    int id;
    String externalId;
    LocalDateTime sent_at;
    Client client;
    String status;
    LocalDateTime deletedAt;
    LocalDateTime  createdAt;
    LocalDateTime updatedAt;
}
