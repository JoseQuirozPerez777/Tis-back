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

    private String nombreHabilidad;

    @Enumerated(EnumType.STRING)
    private TipoHabilidad tipo;

    private int nivelDominio;
    private boolean esPublico = true;

    public enum TipoHabilidad { TECNICA, BLANDA }
}