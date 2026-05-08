package com.teamsys.portafolios.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ExperienciaLaboralRequestDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String nombreEmpresa;

    @NotBlank(message = "El cargo o puesto es obligatorio")
    private String cargoPuesto;

    private String areaProfesional;
    private String especializacion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;
    private boolean actualmenteTrabajoAqui;

    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidadTrabajo; // "REMOTO", "HIBRIDO", etc.

    private String ubicacion;

    @NotBlank(message = "El tipo de contrato es obligatorio")
    private String tipoContrato; // "TIEMPO_COMPLETO", etc.

    // Enviamos una lista de IDs de las tecnologías seleccionadas
    private List<Long> tecnologiasIds;

    @NotBlank(message = "La descripción del proyecto es obligatoria")
    @Size(max = 1000)
    private String descripcionProyecto;

    private String evidenciaLaboralPdfUrl;
    private String proyectoRelacionadoUrl;
}