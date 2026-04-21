package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "habilidades_tecnicas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HabilidadTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false) // Aquí se guarda la "Categoría" técnica
    private Categoria categoria;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private NivelDominio nivelDominio;

    private Integer anosExperiencia;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String certificadoUrl;

    public enum NivelDominio {
        BASICO, INTERMEDIO, AVANZADO, EXPERTO
    }
}