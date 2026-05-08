package com.teamsys.portafolios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TecnologiaResponseDTO {
    private Long id;
    private String nombre;
    private String categoria;
    private String logoUrl;
}