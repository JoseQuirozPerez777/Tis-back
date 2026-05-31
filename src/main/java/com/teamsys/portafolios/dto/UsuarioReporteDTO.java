package com.teamsys.portafolios.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioReporteDTO {
    private Long id;
    private String nombreCompleto;
    private String correo;
    private String profesion;
    private String fechaRegistro;
    private String estado;
}