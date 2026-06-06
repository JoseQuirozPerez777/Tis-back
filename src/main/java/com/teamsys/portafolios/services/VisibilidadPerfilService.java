package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.VisibilidadPerfilDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.entities.VisibilidadPerfil;
import com.teamsys.portafolios.repositories.VisibilidadPerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisibilidadPerfilService {

    @Autowired
    private VisibilidadPerfilRepository visibilidadPerfilRepository;

    // Obtener la configuración actual de los ojos
    public VisibilidadPerfilDTO obtenerVisibilidadConfig(Usuario usuario) {
        VisibilidadPerfil config = visibilidadPerfilRepository.findById(usuario.getIdUsuario())
                .orElseGet(() -> visibilidadPerfilRepository.save(
                        VisibilidadPerfil.builder()
                                .usuario(usuario)
                                .nombreUsr(true)
                                .correoUsr(true)
                                .biografiaUsr(true)
                                .telefonoUsr(true)
                                .direccionUsr(true)
                                .profesionUsr(true)
                                .build()
                ));

        return VisibilidadPerfilDTO.builder()
                .nombreUsr(config.isNombreUsr())
                .correoUsr(config.isCorreoUsr())
                .biografiaUsr(config.isBiografiaUsr())
                .telefonoUsr(config.isTelefonoUsr())
                .direccionUsr(config.isDireccionUsr())
                .profesionUsr(config.isProfesionUsr())
                .build();
    }

    // Guardar los cambios masivos cuando hacen clic en "Aplicar cambios"
    @Transactional
    public void guardarVisibilidadConfig(Usuario usuario, VisibilidadPerfilDTO dto) {
        VisibilidadPerfil config = visibilidadPerfilRepository.findById(usuario.getIdUsuario())
                .orElse(VisibilidadPerfil.builder().usuario(usuario).build());

        config.setNombreUsr(dto.isNombreUsr());
        config.setCorreoUsr(dto.isCorreoUsr());
        config.setBiografiaUsr(dto.isBiografiaUsr());
        config.setTelefonoUsr(dto.isTelefonoUsr());
        config.setDireccionUsr(dto.isDireccionUsr());
        config.setProfesionUsr(dto.isProfesionUsr());

        visibilidadPerfilRepository.save(config);
    }
}