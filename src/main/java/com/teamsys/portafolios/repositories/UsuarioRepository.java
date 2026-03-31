package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Esto servirá para validar que el correo no se repita
    boolean existsByCorreo(String correo);

    Optional<Usuario> findByCorreo(String correo);
}