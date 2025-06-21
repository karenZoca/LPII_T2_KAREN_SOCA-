package com.cibertec.examenT2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "detalle_alquiler")
@IdClass(DetalleAlquiler.DetalleAlquilerId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleAlquiler {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alquiler", nullable = false)
    private Alquiler alquiler;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pelicula", nullable = false)
    private Pelicula pelicula;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleAlquilerId implements Serializable {
        private Long alquiler;
        private Long pelicula;
    }
}
