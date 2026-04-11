package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "habilidades")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Habilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHabilidad;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    private String nombreHabilidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHabilidad tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private NivelDominio nivelDominio;

    @Column(nullable = true)
    private Integer anosExperiencia;

    // Nuevo atributo para certificados (puede ser una URL o descripción)
    @Column(nullable = true)
    private String certificado;

    private boolean esPublico = true;

    // --- Enums ---

    public enum TipoHabilidad {
        TECNICA, BLANDA
    }

    public enum NivelDominio {
        PRINCIPIANTE, INTERMEDIO, AVANZADO, EXPERTO
    }
}