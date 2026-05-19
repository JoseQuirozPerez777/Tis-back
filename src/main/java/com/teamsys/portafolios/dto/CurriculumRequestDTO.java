package com.teamsys.portafolios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El tipo (SUBIDO/GENERADO) es obligatorio")
    private String tipo;

    @NotNull(message = "El estado oficial es obligatorio")
    private Boolean esOficial;

    @NotBlank(message = "La URL de Cloudinary es obligatoria")
    private String urlPdf;
}