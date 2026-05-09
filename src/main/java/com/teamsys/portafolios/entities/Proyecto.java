package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "proyectos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Recomendado para facilitar la creación de objetos en pruebas
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    /**
     * ERROR CORREGIDO:
     * Tenías 'private String List <String>urlImagen;'.
     * Para persistir una lista de Strings simple en JPA se usa @ElementCollection.
     */
    @ElementCollection
    @CollectionTable(name = "proyecto_imagenes", joinColumns = @JoinColumn(name = "proyecto_id"))
    @Column(name = "url_imagen")
    private List<String> urlsImagenes;

    /**
     * MEJORA DE DISEÑO:
     * Ya que tienes una entidad 'Tecnologia', lo ideal es una relación ManyToMany
     * en lugar de un String plano, para poder filtrar proyectos por tecnología.
     */
    @ManyToMany
    @JoinTable(
            name = "proyecto_tecnologias",
            joinColumns = @JoinColumn(name = "id_proyecto"),
            inverseJoinColumns = @JoinColumn(name = "id_tecnologia")
    )
    private List<Tecnologia> tecnologias;

    private String enlaceGithub;
    private String enlaceDemo;

    @Column(nullable = false)
    private boolean esPublico = true;
}