package com.teamsys.portafolios.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumDTO {
    private Long idCurriculum;
    private String titulo;
    private String urlPdf;
    private LocalDateTime fechaCreacion;
    private String tipo;
    private boolean esOficial;
}