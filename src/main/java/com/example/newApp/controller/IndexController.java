package com.example.newApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.*;
@Controller
public class IndexController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("error", false); 
        return "loginAdmin";  
    }

    @GetMapping("/access-denied")
    public String pageAccesInterdit(){
        return "access-denied";
    }
}