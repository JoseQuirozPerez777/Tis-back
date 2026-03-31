package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Método necesario para buscar el rol por su nombre (ROLE_USER)
    Optional<Rol> findByNombreRol(String nombreRol);
}