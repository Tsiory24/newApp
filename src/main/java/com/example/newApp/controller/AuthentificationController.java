package com.example.newApp.controller;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.newApp.service.AuthentificationService;
import com.example.newApp.model.User;
import com.example.newApp.util.ResponseUtil;

import jakarta.servlet.http.HttpSession;


@RequestMapping("/authentification")
@Controller
public class AuthentificationController {
    private final String apiUrl = "http://localhost/api/authentification";
    private final AuthentificationService authentificationService;

    @Autowired
    public AuthentificationController(AuthentificationService authentificationService) {
        this.authentificationService = authentificationService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "authentification/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            ResponseUtil response = authentificationService.login(apiUrl + "/login", email, password);

            if ("success".equals(response.getStatus()) && response.getData() != null) {
                Map<String, Object> data = response.getData();

                // Récupération des valeurs
                String token = (String) data.get("token");
                Map<String, Object> userData = (Map<String, Object>) data.get("user");

                // Création de l'objet User
                User user = new User();
                user.setName((String) userData.get("name"));
                user.setEmail((String) userData.get("email"));
                user.setToken(token);

                // Stockage en session
                session.setAttribute("token", token);
                session.setAttribute("user", user);

                return "redirect:/admin/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("message", response.getError() != null ? response.getError() : "Identifiants incorrects !");
                return "redirect:/authentification/login";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erreur interne : " + e.getMessage());
            return "redirect:/authentification/login";
        }
    }

    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
