package com.cibertec.examenT2.repository;

import com.cibertec.examenT2.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByStockGreaterThan(int stock);
}