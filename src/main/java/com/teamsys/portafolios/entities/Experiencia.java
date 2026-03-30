package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "experiencias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Experiencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExperiencia;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String institucionEmpresa;
    private String cargoTitulo;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    private TipoExperiencia tipo; // LABORAL o ACADEMICA

    private boolean esLogro = false;
    private boolean esPublico = true; // Control de visibilidad

    public enum TipoExperiencia { LABORAL, ACADEMICA }
}