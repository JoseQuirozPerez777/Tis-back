package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Curriculum;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.entities.VisibilidadPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisibilidadPerfilRepository extends JpaRepository<VisibilidadPerfil, Long> {
    List<VisibilidadPerfil> findByUsuario(Usuario usuario);
}