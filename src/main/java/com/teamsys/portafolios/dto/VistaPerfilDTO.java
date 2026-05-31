package com.teamsys.portafolios.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VistaPerfilDTO {
    private String nombre;
    private String foto;
    private String profesion;
    private LocalDateTime fechaVisita;
}