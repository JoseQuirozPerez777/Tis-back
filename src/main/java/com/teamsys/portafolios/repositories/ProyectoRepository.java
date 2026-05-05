package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Proyecto;
import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    // Buscar todos los proyectos de un usuario específico
    List<Proyecto> findByUsuario(Usuario usuario);

    // Buscar un proyecto específico asegurando que pertenezca al usuario (seguridad)
    Optional<Proyecto> findByIdProyectoAndUsuario(Long idProyecto, Usuario usuario);
}