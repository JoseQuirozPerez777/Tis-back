package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redes_sociales")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RedSocial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRed;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String nombreRed; // LinkedIn, GitHub, etc.
    private String urlPerfil;
    private boolean esPublico = true;
}