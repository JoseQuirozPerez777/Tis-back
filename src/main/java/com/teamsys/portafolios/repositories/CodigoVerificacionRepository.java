package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.CodigoVerificacion;
import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, Long> {
    // Buscamos el código actual de un usuario específico
    Optional<CodigoVerificacion> findByUsuario(Usuario usuario);
}