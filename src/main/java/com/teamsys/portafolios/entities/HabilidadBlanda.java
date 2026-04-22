package com.teamsys.portafolios.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "habilidades_blandas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HabilidadBlanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false) // Aquí se guarda el "Contexto"
    private Categoria categoria;

    private String evidenciaUrl; // El string/URL que solicitaste
}