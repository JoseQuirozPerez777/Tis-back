package com.teamsys.portafolios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // Importante para que Lombok considere los campos de la clase padre
public class HabilidadBlandaUpdateDTO extends HabilidadBlandaRequestDTO {

    @NotNull(message = "El ID es necesario para actualizar")
    private Long id;
}