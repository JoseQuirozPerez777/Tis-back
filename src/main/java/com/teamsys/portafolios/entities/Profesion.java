package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "profesiones")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfesion;

    @Column(nullable = false, unique = true)
    private String nombreProfesion;

    // Relación inversa (opcional pero recomendada)
    @OneToMany(mappedBy = "profesion")
    private Set<Usuario> usuarios;
}