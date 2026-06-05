package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.Curriculum;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.entities.VistaPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VistaPerfilRepository extends JpaRepository<VistaPerfil, Long> {

    // Contar total de visitas de un perfil
    long countByPerfil(Usuario perfil);

    // Obtener las visitas ordenadas por fecha descendente
    List<VistaPerfil> findByPerfilOrderByFechaVisitaDesc(Usuario perfil);

    // Buscar la última visita de un usuario registrado a un perfil
    Optional<VistaPerfil> findFirstByPerfilAndVisitanteOrderByFechaVisitaDesc(Usuario perfil, Usuario visitante);

    // Buscar la última visita anónima dentro de una ventana de tiempo (opcional/avanzado)
    @Query("SELECT v FROM VistaPerfil v WHERE v.perfil = :perfil AND v.visitante IS NULL AND v.fechaVisita > :limite ORDER BY v.fechaVisita DESC")
    List<VistaPerfil> findRecientesAnonimas(@Param("perfil") Usuario perfil, @Param("limite") LocalDateTime limite);
}