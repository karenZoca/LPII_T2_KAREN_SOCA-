package com.cibertec.examenT2.controller;

import com.cibertec.examenT2.model.*;
import com.cibertec.examenT2.repository.DetalleAlquilerRepository;
import com.cibertec.examenT2.service.AlquilerService;
import com.cibertec.examenT2.service.ClienteService;
import com.cibertec.examenT2.service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/alquileres")
@RequiredArgsConstructor
public class AlquilerController {

    private final AlquilerService alquilerService;
    private final ClienteService clienteService;
    private final PeliculaService peliculaService;
    private final DetalleAlquilerRepository detalleAlquilerRepository;
    
    private final List<Alquiler.EstadoAlquiler> estados = Arrays.asList(
        Alquiler.EstadoAlquiler.ACTIVO,
        Alquiler.EstadoAlquiler.DEVUELTO,
        Alquiler.EstadoAlquiler.RETRASADO
    );

    @GetMapping
    public String listarAlquileres(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Alquiler.EstadoAlquiler estado,
            Model model) {
        
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Alquiler> paginaAlquileres;
        
        if (clienteId != null && estado != null) {
            paginaAlquileres = alquilerService.buscarPorClienteYEstado(clienteId, estado, pageable);
        } else if (clienteId != null) {
            paginaAlquileres = alquilerService.buscarPorCliente(clienteId, pageable);
        } else if (estado != null) {
            paginaAlquileres = alquilerService.buscarPorEstado(estado, pageable);
        } else {
            paginaAlquileres = alquilerService.listarTodos(pageable);
        }
        
        model.addAttribute("alquileres", paginaAlquileres.getContent());
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("estados", estados);
        model.addAttribute("detalleAlquilerRepository", detalleAlquilerRepository);
        
        // Atributos para paginación
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginaAlquileres.getTotalPages());
        model.addAttribute("tamanoPagina", size);
        
        return "alquileres/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("peliculas", peliculaService.listarDisponibles());
        model.addAttribute("alquiler", Alquiler.builder()
                .fecha(LocalDate.now())
                .estado(Alquiler.EstadoAlquiler.ACTIVO)
                .build());
        return "alquileres/formulario";
    }

    @PostMapping("/guardar")
    public String guardarAlquiler(
            @Valid @ModelAttribute("alquiler") Alquiler alquiler,
            @RequestParam("peliculas") List<Long> idsPeliculas,
            @RequestParam("cantidades") List<Integer> cantidades,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.listarTodos());
            model.addAttribute("peliculas", peliculaService.listarDisponibles());
            return "alquileres/formulario";
        }

        // Validar stock antes de proceder
        List<String> erroresStock = validarStockDisponible(idsPeliculas, cantidades);
        if (!erroresStock.isEmpty()) {
            model.addAttribute("clientes", clienteService.listarTodos());
            model.addAttribute("peliculas", peliculaService.listarDisponibles());
            model.addAttribute("error", String.join("<br>", erroresStock));
            return "alquileres/formulario";
        }

        // Crear detalles del alquiler
        List<DetalleAlquiler> detalles = new ArrayList<>();
        double total = 0;
        
        for (int i = 0; i < idsPeliculas.size(); i++) {
            Pelicula pelicula = peliculaService.buscarPorId(idsPeliculas.get(i));
            int cantidad = cantidades.get(i);
            
            detalles.add(DetalleAlquiler.builder()
                    .alquiler(alquiler)
                    .pelicula(pelicula)
                    .cantidad(cantidad)
                    .build());
            
            total += cantidad * pelicula.getPrecio();
        }

        alquiler.setTotal(total);
        alquiler.setEstado(Alquiler.EstadoAlquiler.ACTIVO);
        
        alquilerService.registrarAlquiler(alquiler, detalles);

        redirectAttributes.addFlashAttribute("mensaje", "Alquiler registrado con éxito");
        return "redirect:/alquileres";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Alquiler alquiler = alquilerService.buscarPorId(id);
        if (alquiler == null) {
            return "redirect:/alquileres";
        }
        
        model.addAttribute("alquiler", alquiler);
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("peliculas", peliculaService.listarTodos());
        model.addAttribute("detalles", detalleAlquilerRepository.findByAlquiler(alquiler));
        
        return "alquileres/formulario-editar";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarAlquiler(
            @PathVariable Long id,
            @Valid @ModelAttribute("alquiler") Alquiler alquiler,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.listarTodos());
            model.addAttribute("peliculas", peliculaService.listarTodos());
            model.addAttribute("detalles", detalleAlquilerRepository.findByAlquiler(alquiler));
            return "alquileres/formulario-editar";
        }
        
        alquilerService.guardar(alquiler);
        redirectAttributes.addFlashAttribute("mensaje", "Alquiler actualizado con éxito");
        return "redirect:/alquileres";
    }

    @GetMapping("/devolver/{id}")
    public String devolverAlquiler(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alquilerService.marcarComoDevuelto(id);
            redirectAttributes.addFlashAttribute("mensaje", "Alquiler marcado como devuelto");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al marcar como devuelto: " + e.getMessage());
        }
        return "redirect:/alquileres";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAlquiler(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alquilerService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Alquiler eliminado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar alquiler: " + e.getMessage());
        }
        return "redirect:/alquileres";
    }

    private List<String> validarStockDisponible(List<Long> idsPeliculas, List<Integer> cantidades) {
        List<String> errores = new ArrayList<>();
        
        for (int i = 0; i < idsPeliculas.size(); i++) {
            Pelicula pelicula = peliculaService.buscarPorId(idsPeliculas.get(i));
            int cantidad = cantidades.get(i);
            
            if (pelicula == null) {
                errores.add("La película seleccionada no existe");
            } else if (pelicula.getStock() < cantidad) {
                errores.add("No hay suficiente stock para " + pelicula.getTitulo() + 
                           " (Stock disponible: " + pelicula.getStock() + ")");
            }
        }
        
        return errores;
    }
}