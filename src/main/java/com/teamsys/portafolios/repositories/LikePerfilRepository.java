package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.LikePerfil;
import com.teamsys.portafolios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikePerfilRepository extends JpaRepository<LikePerfil, Long> {

    @Query("""
        SELECT COUNT(lp) > 0
        FROM LikePerfil lp
        WHERE lp.perfil = :perfil
        AND lp.usuarioLike = :usuarioLike
    """)
    boolean existsByPerfilAndUsuarioLike(
            @Param("perfil") Usuario perfil,
            @Param("usuarioLike") Usuario usuarioLike
    );

    @Query("""
        SELECT lp
        FROM LikePerfil lp
        WHERE lp.perfil = :perfil
        AND lp.usuarioLike = :usuarioLike
    """)
    Optional<LikePerfil> findByPerfilAndUsuarioLike(
            @Param("perfil") Usuario perfil,
            @Param("usuarioLike") Usuario usuarioLike
    );

    List<LikePerfil> findByPerfilOrderByFechaLikeDesc(Usuario perfil);

    long countByPerfil(Usuario perfil);
}