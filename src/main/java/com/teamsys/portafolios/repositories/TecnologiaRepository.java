package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Tecnologia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TecnologiaRepository extends JpaRepository<Tecnologia, Long> {
    // Para filtrar el catálogo por tipo si lo necesitas en el futuro
    List<Tecnologia> findByCategoria(String categoria);

    // Para validar que no se repitan nombres al crear nuevas
    boolean existsByNombre(String nombre);
}