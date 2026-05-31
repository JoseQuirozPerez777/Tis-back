package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vistas_perfil")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VistaPerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El dueño del portafolio que recibe la visita
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Usuario perfil;

    // El usuario que visita (puede ser null si no está autenticado)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitante_id", nullable = true)
    private Usuario visitante;

    private LocalDateTime fechaVisita;
}