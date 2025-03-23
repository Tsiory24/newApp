package com.example.newApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class DashboardController {
    private final String apiUrl = "http://localhost/api/dashboard";

    @GetMapping("/dashboard")
    public String dashboard(){
        return "admin/pages/dashboard";
    }
}
