package com.cibertec.examenT2.controller;

import com.cibertec.examenT2.service.AlquilerService;
import com.cibertec.examenT2.service.ClienteService;
import com.cibertec.examenT2.service.PeliculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ClienteService clienteService;
    private final PeliculaService peliculaService;
    private final AlquilerService alquilerService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("peliculas", peliculaService.listarTodas());
        model.addAttribute("alquileres", alquilerService.listarTodos());
        return "index";
    }
}