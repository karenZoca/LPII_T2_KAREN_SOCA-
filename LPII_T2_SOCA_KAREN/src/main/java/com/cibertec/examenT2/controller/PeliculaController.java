package com.cibertec.examenT2.controller;

import com.cibertec.examenT2.model.Pelicula;
import com.cibertec.examenT2.service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/peliculas")
@RequiredArgsConstructor
public class PeliculaController {

    private final PeliculaService peliculaService;
    
    // Lista de géneros predefinidos
    private final List<String> generos = Arrays.asList(
        "Acción", "Aventura", "Comedia", "Drama", "Ciencia Ficción",
        "Fantasía", "Terror", "Romance", "Animación", "Documental"
    );

    @GetMapping
    public String listarPeliculas(Model model) {
        model.addAttribute("peliculas", peliculaService.listarTodas());
        return "peliculas/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pelicula", new Pelicula());
        model.addAttribute("generos", generos);
        return "peliculas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarPelicula(@Valid @ModelAttribute("pelicula") Pelicula pelicula, 
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("generos", generos);
            return "peliculas/formulario";
        }
        
        peliculaService.guardar(pelicula);
        redirectAttributes.addFlashAttribute("mensaje", 
            pelicula.getId_Pelicula() != null ? 
            "Película actualizada con éxito" : "Película registrada con éxito");
        return "redirect:/peliculas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Pelicula pelicula = peliculaService.buscarPorId(id);
        if (pelicula == null) {
            return "redirect:/peliculas";
        }
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("generos", generos);
        return "peliculas/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPelicula(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        peliculaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Película eliminada con éxito");
        return "redirect:/peliculas";
    }
}