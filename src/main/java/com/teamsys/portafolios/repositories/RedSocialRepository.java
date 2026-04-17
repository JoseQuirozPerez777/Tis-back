package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.RedSocial;
import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedSocialRepository extends JpaRepository<RedSocial, Long> {

    List<RedSocial> findByUsuario(Usuario usuario);

    Optional<RedSocial> findByIdRedAndUsuario(Long idRed, Usuario usuario);
}