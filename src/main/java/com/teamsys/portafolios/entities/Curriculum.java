package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "curriculums")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCurriculum;

    @Column(nullable = false)
    private String titulo; // Nombre personalizado del CV

    @Column(nullable = false)
    private String urlPdf; // Ruta del archivo local o de almacenamiento en nube

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = false)
    private String tipo; // "SUBIDO" o "GENERADO"

    @Column(nullable = false)
    private boolean esOficial = false; // El fondo verde o etiqueta "Actual"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}