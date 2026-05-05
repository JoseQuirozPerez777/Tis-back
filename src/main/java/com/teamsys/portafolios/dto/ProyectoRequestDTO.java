package com.teamsys.portafolios.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProyectoRequestDTO {
    private String titulo;
    private String descripcion;
    private String tecnologias;
    private String enlaceGithub;
    private String enlaceDemo;
    private String urlImagen;
    private boolean esPublico;
}