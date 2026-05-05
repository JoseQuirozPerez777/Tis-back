package com.teamsys.portafolios.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FormacionRequestDTO {
    private String institucion;
    private String tituloObtenido;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String descripcion;
    private boolean enCurso;
    private String urlImagen; // Se añade al request
}