package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Profesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfesionRepository extends JpaRepository<Profesion, Long> {

    // Busca la profesión exacta por nombre
    Optional<Profesion> findByNombreProfesion(String nombreProfesion);

    // Opcional: Si quieres buscar nombres que contengan parte del texto (ej: "Ing" -> "Ingeniero")
    // List<Profesion> findByNombreProfesionContainingIgnoreCase(String nombre);
}