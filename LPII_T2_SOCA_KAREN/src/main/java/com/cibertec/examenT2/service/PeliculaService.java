package com.cibertec.examenT2.service;

import com.cibertec.examenT2.model.Pelicula;
import com.cibertec.examenT2.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;

    public List<Pelicula> listarDisponibles() {
        return peliculaRepository.findByStockGreaterThan(0);
    }

    public List<Pelicula> listarTodos() {
        return peliculaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pelicula> listarTodas() {
        return peliculaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pelicula buscarPorId(Long id) {
        return peliculaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Pelicula guardar(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    @Transactional
    public void eliminar(Long id) {
        peliculaRepository.deleteById(id);
    }

    @Transactional
    public void actualizarStock(Long id_Pelicula, int cantidad) {
        peliculaRepository.findById(id_Pelicula).ifPresent(pelicula -> {
            pelicula.setStock(pelicula.getStock() + cantidad);
            peliculaRepository.save(pelicula);
        });
    }
}