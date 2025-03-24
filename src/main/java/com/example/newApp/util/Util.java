package com.example.newApp.util;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Util {
    public static LocalDateTime parseDate(Object dateObj, DateTimeFormatter formatter) {
        if (dateObj == null) {
            return null;
        }

        String dateStr = dateObj.toString();
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
}
