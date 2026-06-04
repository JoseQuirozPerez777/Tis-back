package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "likes_perfil",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"perfil_id", "usuario_like_id"})}
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikePerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El dueño del portafolio que recibe el like
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Usuario perfil;

    // El usuario que da el like (OBLIGATORIO, no anónimos)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_like_id", nullable = false)
    private Usuario usuarioLike;

    private LocalDateTime fechaLike;
}