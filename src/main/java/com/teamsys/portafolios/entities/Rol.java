package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    @Column(nullable = false, unique = true)
    private String nombreRol;
}