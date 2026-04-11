package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Habilidad;
import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabilidadRepository extends JpaRepository<Habilidad, Long> {

    /**
     * Busca todas las habilidades asociadas a un usuario específico.
     * Útil para cargar el perfil del usuario logueado.
     */
    List<Habilidad> findByUsuario(Usuario usuario);

    /**
     * También puedes buscar por tipo (TECNICA o BLANDA)
     * si necesitas separarlas en el frontend.
     */
    List<Habilidad> findByUsuarioAndTipo(Usuario usuario, Habilidad.TipoHabilidad tipo);
}