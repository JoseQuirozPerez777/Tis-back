package com.teamsys.portafolios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPerfilDTO {
    private String nombre;
    private String biografia;
    private Long idProfesion;
    private String foto;
}