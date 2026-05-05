package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proyectos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProyecto;

    // Usamos FetchType.LAZY para que no cargue el usuario a menos que sea necesario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 2000) // Descripción más amplia para dar detalles
    private String descripcion;

    // Campo para tecnologías: ejemplo "Java, Spring Boot, MySQL"
    @Column(name = "tecnologias_usadas")
    private String tecnologias;

    private String enlaceGithub;
    private String enlaceDemo;

    // Para mostrar una miniatura o captura del proyecto
    private String urlImagen;

    @Column(nullable = false)
    private boolean esPublico = true;

}