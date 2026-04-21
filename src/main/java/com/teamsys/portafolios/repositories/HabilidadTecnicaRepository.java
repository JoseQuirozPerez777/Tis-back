package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.HabilidadTecnica;
import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabilidadTecnicaRepository extends JpaRepository<HabilidadTecnica, Long> {

    // Obtener todas las habilidades técnicas de un usuario
    List<HabilidadTecnica> findByUsuario(Usuario usuario);

    // Opcional: Buscar habilidades técnicas por categoría (ej: todas las de 'Backend')
    List<HabilidadTecnica> findByUsuarioAndCategoria_Nombre(Usuario usuario, String nombreCategoria);
}