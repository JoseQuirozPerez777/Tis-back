package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Curriculum;
import com.teamsys.portafolios.entities.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    
    // Obtiene todos los CVs del usuario ordenados del más reciente al más antiguo
    List<Curriculum> findByUsuarioIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

        // Obtener todas las habilidades blandas de un usuario
    List<Curriculum> findByUsuario(Usuario usuario);


    java.util.Optional<Curriculum> findByUsuarioAndEsOficialTrue(Usuario usuario);
}