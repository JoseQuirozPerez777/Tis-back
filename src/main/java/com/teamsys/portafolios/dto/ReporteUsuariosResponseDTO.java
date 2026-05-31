package com.teamsys.portafolios.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteUsuariosResponseDTO {
    private long totalUsuarios;
    private long usuariosActivos;
    private long usuariosInactivos;
    private List<UsuarioReporteDTO> usuarios;
}