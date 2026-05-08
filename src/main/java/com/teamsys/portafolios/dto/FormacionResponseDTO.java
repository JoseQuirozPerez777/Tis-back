package com.teamsys.portafolios.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormacionResponseDTO {
    private Long id;
    private String institucion;
    private String tituloObtenido;
    private String nivel;
    private String area;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String descripcion;
    private String estado;
    private String urlImagen;
}