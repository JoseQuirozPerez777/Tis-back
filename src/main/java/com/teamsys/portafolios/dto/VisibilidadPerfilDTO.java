package com.teamsys.portafolios.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisibilidadPerfilDTO {
    private boolean nombreUsr;
    private boolean correoUsr;
    private boolean biografiaUsr;
    private boolean telefonoUsr;
    private boolean direccionUsr;
    private boolean profesionUsr;
    //private boolean universidadUsr;
}