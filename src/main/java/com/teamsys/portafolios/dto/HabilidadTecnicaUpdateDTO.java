package com.teamsys.portafolios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HabilidadTecnicaUpdateDTO extends HabilidadTecnicaRequestDTO {

    @NotNull(message = "El ID es obligatorio para actualizar la habilidad")
    private Long id;
}