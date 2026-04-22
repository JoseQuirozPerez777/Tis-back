package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.ExperienciaLaboral;
import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExperienciaLaboralRepository extends JpaRepository<ExperienciaLaboral, Long> {
    List<ExperienciaLaboral> findByUsuario(Usuario usuario);
}