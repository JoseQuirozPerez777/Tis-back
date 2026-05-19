package com.teamsys.portafolios.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class ProyectoRequestDTO {
    private String titulo;
    private String rolProyecto;
    private String descripcion;
    private List<String> urlsAdicionales;
    private List<String> urlsImagenes;
    private List<Long> tecnologiaIds;
    private List<String> nuevasTecnologias;
    private String enlaceGithub;
    private String enlaceDemo;
    private boolean destacar; // Agregado
    private String urlPdf;      // Agregado
    private String fechaInicio;
    private String fechaFinalizacion;
    private String estadoProyecto;
    private boolean esPublico;
    private Long idUsuario;
}