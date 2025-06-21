package com.cibertec.examenT2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/examenT2")
    public String home() {
        return "index"; // Asegúrate de tener templates/index.html
    }
}