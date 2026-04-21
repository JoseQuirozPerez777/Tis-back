package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.HabilidadBlanda;
import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabilidadBlandaRepository extends JpaRepository<HabilidadBlanda, Long> {

    // Obtener todas las habilidades blandas de un usuario
    List<HabilidadBlanda> findByUsuario(Usuario usuario);

    // Opcional: Filtrar habilidades blandas por el nombre del contexto (categoría)
    List<HabilidadBlanda> findByUsuarioAndCategoria_Nombre(Usuario usuario, String nombreContexto);
}