package com.teamsys.portafolios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisibilidadElementosMasivaDTO {
    private List<CambioVisibilidadDTO> experiencias = new ArrayList<>();
    private List<CambioVisibilidadDTO> formaciones = new ArrayList<>();
    private List<CambioVisibilidadDTO> habilidadesTecnicas = new ArrayList<>();
    private List<CambioVisibilidadDTO> habilidadesBlandas = new ArrayList<>();
    private List<CambioVisibilidadDTO> proyectos = new ArrayList<>();
    private List<CambioVisibilidadDTO> redesSociales = new ArrayList<>();
}