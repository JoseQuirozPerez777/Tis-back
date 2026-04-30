package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String foto;

    @ManyToOne
    @JoinColumn(name = "id_profesion")
    private Profesion profesion;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Column(nullable = true)
    private String telefono; 

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Relación muchos a muchos para Roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles;

    private int intentosFallidos = 0;
    private LocalDateTime fechaUltimoIntentoFallido;
    private LocalDateTime fechaBloqueo;


}