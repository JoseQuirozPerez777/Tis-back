package com.teamsys.portafolios.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class ProyectoResponseDTO {
    private Long idProyecto;
    private String titulo;
    private String rolProyecto; // Agregado
    private String descripcion;
    private List<String> urlsAdicionales; // Agregado
    private List<String> urlsImagenes;
    private List<Long> tecnologiaIds;
    private String enlaceGithub;
    private String enlaceDemo;
    private String fechaInicio; // Agregado
    private String fechaFinalizacion; // Agregado
    private String estadoProyecto; // Agregado
    private boolean esPublico;
    private Long idUsuario;
}