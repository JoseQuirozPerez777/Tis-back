package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "experiencias_laborales")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor
public class ExperienciaLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(nullable = false)
    private String empresa;

    @Column(nullable = false)
    private String cargo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Campo extra útil para el Frontend: 
     * Indica si el usuario trabaja actualmente en este lugar.
     */
    private boolean esTrabajoActual;
}