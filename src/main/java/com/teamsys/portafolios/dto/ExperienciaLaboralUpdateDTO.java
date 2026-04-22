package com.teamsys.portafolios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExperienciaLaboralUpdateDTO extends ExperienciaLaboralRequestDTO {
    
    @NotNull(message = "El ID es obligatorio para actualizar la experiencia")
    private Long id;
}