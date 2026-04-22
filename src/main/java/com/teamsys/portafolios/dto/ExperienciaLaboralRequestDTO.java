package com.teamsys.portafolios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExperienciaLaboralRequestDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String empresa;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    // Opcional si es el trabajo actual, pero según tus criterios, 
    // si el "periodo" total es obligatorio, podrías requerir ambos.
    private LocalDate fechaFin;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private boolean esTrabajoActual;
}