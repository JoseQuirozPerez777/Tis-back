package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tecnologias")
@Getter @Setter @NoArgsConstructor
public class Tecnologia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // Ej: "React", "Java", "Spring Boot"

    private String categoria; // Ej: "Frontend", "Backend", "Base de Datos"

    private String logoUrl; // Para mostrar el iconito en los resultados de búsqueda
}
