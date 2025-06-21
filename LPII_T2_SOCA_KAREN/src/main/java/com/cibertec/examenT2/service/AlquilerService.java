package com.cibertec.examenT2.service;

import com.cibertec.examenT2.model.*;
import com.cibertec.examenT2.repository.AlquilerRepository;
import com.cibertec.examenT2.repository.DetalleAlquilerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final DetalleAlquilerRepository detalleAlquilerRepository;
    private final PeliculaService peliculaService;

    public List<Alquiler> listarTodos() {
        return alquilerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Alquiler> listarTodos(Pageable pageable) {
        return alquilerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Alquiler> buscarPorCliente(Long idCliente, Pageable pageable) {
    return alquilerRepository.findByClienteIdCliente(idCliente, pageable);
   }

    @Transactional(readOnly = true)
    public Page<Alquiler> buscarPorClienteYEstado(Long idCliente, Alquiler.EstadoAlquiler estado, Pageable pageable) {
    return alquilerRepository.findByClienteIdClienteAndEstado(idCliente, estado, pageable);
   }

   @Transactional(readOnly = true)
   public Page<Alquiler> buscarPorEstado(Alquiler.EstadoAlquiler estado, Pageable pageable) {
    return alquilerRepository.findByEstado(estado, pageable);
   }

    @Transactional(readOnly = true)
    public Alquiler buscarPorId(Long id) {
        return alquilerRepository.findById(id).orElse(null);
    }

    @Transactional
    public Alquiler guardar(Alquiler alquiler) {
        return alquilerRepository.save(alquiler);
    }

    @Transactional
    public void eliminar(Long id) {
        // Primero eliminamos los detalles asociados
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));
        
        // Devolvemos el stock si el alquiler estaba activo
        if (alquiler.getEstado() == Alquiler.EstadoAlquiler.ACTIVO || 
            alquiler.getEstado() == Alquiler.EstadoAlquiler.RETRASADO) {
            devolverStock(alquiler);
        }
        
        detalleAlquilerRepository.deleteByAlquiler(alquiler);
        alquilerRepository.delete(alquiler);
    }

    @Transactional
    public Alquiler registrarAlquiler(Alquiler alquiler, List<DetalleAlquiler> detalles) {
        alquiler.setFecha(LocalDate.now());
        alquiler.setEstado(Alquiler.EstadoAlquiler.ACTIVO);
        Alquiler alquilerGuardado = alquilerRepository.save(alquiler);

        for (DetalleAlquiler detalle : detalles) {
            detalle.setAlquiler(alquilerGuardado);
            detalleAlquilerRepository.save(detalle);
            
            Pelicula pelicula = detalle.getPelicula();
            pelicula.setStock(pelicula.getStock() - detalle.getCantidad());
            peliculaService.guardar(pelicula);
        }

        return alquilerGuardado;
    }

    @Transactional
    public void marcarComoDevuelto(Long idAlquiler) {
        Alquiler alquiler = alquilerRepository.findById(idAlquiler)
                .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));
        
        if (alquiler.getEstado() != Alquiler.EstadoAlquiler.DEVUELTO) {
            alquiler.setEstado(Alquiler.EstadoAlquiler.DEVUELTO);
            alquilerRepository.save(alquiler);
            
            devolverStock(alquiler);
        }
    }

    private void devolverStock(Alquiler alquiler) {
        List<DetalleAlquiler> detalles = detalleAlquilerRepository.findByAlquiler(alquiler);
        for (DetalleAlquiler detalle : detalles) {
            peliculaService.actualizarStock(detalle.getPelicula().getId_Pelicula(), detalle.getCantidad());
        }
    }
}