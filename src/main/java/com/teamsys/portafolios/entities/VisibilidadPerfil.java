package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "visibilidad_perfil")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisibilidadPerfil {

    @Id
    private Long idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private boolean nombreUsr = true;
    private boolean correoUsr = true;
    private boolean biografiaUsr = true;
    private boolean telefonoUsr = true;
    private boolean direccionUsr = true;
    private boolean profesionUsr = true;
    //private boolean universidadUsr = true; // Aplica para estudios/formación en general
}