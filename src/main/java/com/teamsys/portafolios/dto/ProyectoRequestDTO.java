package com.teamsys.portafolios.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class ProyectoRequestDTO {
    private String titulo;
    private String descripcion;

    // Si decides usar IDs de la entidad Tecnologia:
    private List<Long> tecnologiaIds;
    // O si prefieres mantenerlo como texto simple por ahora:

    private String enlaceGithub;
    private String enlaceDemo;

    // Corregido a Lista para coincidir con la entidad
    private List<String> urlsImagenes;

    private boolean esPublico;

    // Necesario para asociar el proyecto al usuario creador
    private Long idUsuario;
}