package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.VisibilidadPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisibilidadPerfilRepository extends JpaRepository<VisibilidadPerfil, Long> {
}