package com.teamsys.portafolios.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class ProyectoResponseDTO {
    private Long idProyecto;
    private String titulo;
    private String descripcion;

    // Podrías devolver los nombres de las tecnologías o los objetos DTO de Tecnología
    private List<Long> tecnologiaIds;

    private String enlaceGithub;
    private String enlaceDemo;

    // Lista de URLs de imágenes
    private List<String> urlsImagenes;

    private boolean esPublico;

    // Opcional: ID del dueño para navegación en el front
    private Long idUsuario;
}