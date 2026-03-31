package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Profesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesionRepository extends JpaRepository<Profesion, Long> {
}