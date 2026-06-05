package com.teamsys.portafolios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortafolioVisibilidadElementosDTO {
    private List<ElementoVisibilidadDTO> experiencias;
    private List<ElementoVisibilidadDTO> formaciones;
    private List<ElementoVisibilidadDTO> habilidadesTecnicas;
    private List<ElementoVisibilidadDTO> habilidadesBlandas;
    private List<ElementoVisibilidadDTO> proyectos;
    private List<ElementoVisibilidadDTO> redesSociales;
}