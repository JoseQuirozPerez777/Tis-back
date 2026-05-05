package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.FormacionAcademica;
import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormacionRepository extends JpaRepository<FormacionAcademica, Long> {
    List<FormacionAcademica> findByUsuario(Usuario usuario);
    Optional<FormacionAcademica> findByIdAndUsuario(Long id, Usuario usuario);
}