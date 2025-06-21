package com.cibertec.examenT2.repository;

import com.cibertec.examenT2.model.Alquiler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    Page<Alquiler> findByClienteIdClienteAndEstado(Long idCliente, Alquiler.EstadoAlquiler estado, Pageable pageable);
    Page<Alquiler> findByClienteIdCliente(Long idCliente, Pageable pageable);
    //Page<Alquiler> findByClienteIdAndEstado(Long clienteId, Alquiler.EstadoAlquiler estado, Pageable pageable);
    Page<Alquiler> findByEstado(Alquiler.EstadoAlquiler estado, Pageable pageable);
}
