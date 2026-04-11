package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // JpaRepository ya incluye el método findAll() que necesitamos
}