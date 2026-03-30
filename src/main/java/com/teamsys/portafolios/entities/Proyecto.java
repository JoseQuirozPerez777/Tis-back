package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proyectos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProyecto;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String titulo;
    private String descripcion;
    private String enlaceGithub;
    private String enlaceDemo;
    private boolean esPublico = true; // Control de visibilidad
}