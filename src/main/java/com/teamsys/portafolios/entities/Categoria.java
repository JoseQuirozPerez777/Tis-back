package com.teamsys.portafolios.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categorias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @Column(nullable = false, unique = true)
    private String nombre; // Ej: "Backend", "Frontend" (Técnicas) o "Laboral", "Académico" (Blandas)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Clasificacion clasificacion;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<HabilidadTecnica> habilidadesTecnicas;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<HabilidadBlanda> habilidadesBlandas;

    public enum Clasificacion {
        TECNICA, BLANDA
    }
}