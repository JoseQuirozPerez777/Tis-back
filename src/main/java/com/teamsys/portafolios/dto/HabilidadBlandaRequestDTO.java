package com.teamsys.portafolios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HabilidadBlandaRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El contexto (categoría) es obligatorio")
    private Long idCategoria;

    private String evidenciaUrl;

    private String descripcion;

}