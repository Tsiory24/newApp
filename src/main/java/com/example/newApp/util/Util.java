package com.example.newApp.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Util {
    public static LocalDateTime parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return LocalDateTime.parse(dateStr, formatter);
        } catch (Exception e) {
            System.err.println("Erreur de parsing de la date : " + dateStr);
            return null;
        }
    }
    public static LocalDateTime parseSentAtDate(String dateStr) {
        try {
            // Définir le format exact de la chaîne
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateStr, formatter);
        } catch (Exception e) {
            System.out.println("Erreur de parsing de la date : " + dateStr);
            return null; // Retourner null si la date est invalide
        }
    }

      public static LocalDateTime parseStringWithZTodate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;  // Si la chaîne de date est null ou vide, on retourne null.
        }
        
        try {
            // Format de la date : 2025-03-24T17:26:28.000000Z
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

            // Parsing de la date en LocalDateTime, et ajustement du fuseau horaire UTC
            return LocalDateTime.parse(dateStr, formatter).atOffset(ZoneOffset.UTC).toLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // En cas d'erreur de parsing, on retourne null.
        }
    }
}
