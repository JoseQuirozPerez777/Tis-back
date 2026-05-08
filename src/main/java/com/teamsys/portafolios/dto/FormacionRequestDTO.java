package com.teamsys.portafolios.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormacionRequestDTO {
    private String institucion;
    private String tituloObtenido;
    private String nivel; // Ej: "LICENCIATURA", "MAESTRIA"
    private String area;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String descripcion;
    private String estado; // Ej: "EN_CURSO", "FINALIZADO", "INCOMPLETO"
    private String urlImagen;
}