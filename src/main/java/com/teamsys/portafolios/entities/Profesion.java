package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore // <--- ESTO EVITA EL BUCLE INFINITO
    @OneToMany(mappedBy = "profesion")
    private Set<Usuario> usuarios;
}