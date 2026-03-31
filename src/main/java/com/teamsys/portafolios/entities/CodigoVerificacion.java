package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "codigos_verificacion")
@Getter @Setter @NoArgsConstructor
public class CodigoVerificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @OneToOne // Un código pertenece a un usuario
    @JoinColumn(name = "id_usuario", referencedColumnName = "idUsuario")
    private Usuario usuario;

    // Tipo de código (RECUPERACION_PASSWORD, ACTIVACION_CUENTA, etc.)
    private String tipo;
}
