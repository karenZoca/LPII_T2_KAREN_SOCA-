package com.cibertec.examenT2.repository;

import com.cibertec.examenT2.model.Alquiler;
import com.cibertec.examenT2.model.DetalleAlquiler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleAlquilerRepository extends JpaRepository<DetalleAlquiler, DetalleAlquiler.DetalleAlquilerId> {
    List<DetalleAlquiler> findByAlquiler(Alquiler alquiler);
    void deleteByAlquiler(Alquiler alquiler);
}