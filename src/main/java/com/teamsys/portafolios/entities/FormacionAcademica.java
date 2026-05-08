package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "formaciones_academicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormacionAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(nullable = false)
    private String institucion;

    @Column(nullable = false, name = "titulo_obtenido")
    private String tituloObtenido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private NivelAcademico nivel;

    @Column(nullable = true)
    private String area;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoFormacion estado;

    @Column(name = "url_imagen")
    private String urlImagen;

    // --- ENUMS INTERNOS ---

    public enum NivelAcademico {
        TECNICO,
        LICENCIATURA,
        DIPLOMADO,
        MAESTRIA,
        DOCTORADO
    }

    public enum EstadoFormacion {
        EN_CURSO,
        FINALIZADO,
        INCOMPLETO
    }
}