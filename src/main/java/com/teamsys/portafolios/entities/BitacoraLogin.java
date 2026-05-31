package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bitacora_logins")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BitacoraLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaLogin;

    private String ipOrigen; // Opcional, por si quieres registrar desde dónde ingresó
}