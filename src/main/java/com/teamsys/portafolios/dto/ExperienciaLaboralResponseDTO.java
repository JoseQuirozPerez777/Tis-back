package com.teamsys.portafolios.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ExperienciaLaboralResponseDTO {
    private Long id;
    private String nombreEmpresa;
    private String cargoPuesto;
    private String areaProfesional;
    private String especializacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean actualmenteTrabajoAqui;
    private String modalidadTrabajo;
    private String ubicacion;
    private String tipoContrato;
    private String descripcionProyecto;
    private String evidenciaLaboralPdfUrl;
    private String proyectoRelacionadoUrl;

    // Lista de nombres o DTOs de tecnologías para los "badges" de la UI
    private List<String> nombresTecnologias;
}