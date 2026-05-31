package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.ReporteUsuariosResponseDTO;
import com.teamsys.portafolios.dto.UsuarioReporteDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public ReporteUsuariosResponseDTO obtenerReporteUsuarios(
            String fechaInicio, String fechaFin, String estadoFiltro, String profesion, String busqueda) {

        // Límite de tiempo actual para determinar actividad (30 días atrás)
        LocalDateTime limiteActivo = LocalDateTime.now().minusDays(30);

        Specification<Usuario> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro: Fecha de Registro Desde
            if (fechaInicio != null && !fechaInicio.isBlank()) {
                LocalDate inicio = LocalDate.parse(fechaInicio);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaRegistro"), inicio.atStartOfDay()));
            }

            // Filtro: Fecha de Registro Hasta
            if (fechaFin != null && !fechaFin.isBlank()) {
                LocalDate fin = LocalDate.parse(fechaFin);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaRegistro"), fin.atTime(23, 59, 59)));
            }

            // Filtro Dinámico de Estado basado en 'fechaUltimoLogin'
            if (estadoFiltro != null && !estadoFiltro.isBlank() && !estadoFiltro.equalsIgnoreCase("Todos")) {
                if (estadoFiltro.equalsIgnoreCase("ACTIVO")) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaUltimoLogin"), limiteActivo));
                } else if (estadoFiltro.equalsIgnoreCase("INACTIVO")) {
                    Predicate nuncaLogin = criteriaBuilder.isNull(root.get("fechaUltimoLogin"));
                    Predicate loginAntiguo = criteriaBuilder.lessThan(root.get("fechaUltimoLogin"), limiteActivo);
                    predicates.add(criteriaBuilder.or(nuncaLogin, loginAntiguo));
                }
            }

            // Filtro: Profesión
            if (profesion != null && !profesion.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("profesion").get("nombreProfesion")),
                        "%" + profesion.toLowerCase() + "%"
                ));
            }

            // Filtro: Búsqueda por Nombre completo o Correo electrónico
            if (busqueda != null && !busqueda.isBlank()) {
                String pattern = "%" + busqueda.toLowerCase() + "%";
                Predicate nom = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), pattern);
                Predicate corr = criteriaBuilder.like(criteriaBuilder.lower(root.get("correo")), pattern);
                predicates.add(criteriaBuilder.or(nom, corr));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Ejecutar búsqueda con filtros acumulados
        List<Usuario> usuariosFiltrados = usuarioRepository.findAll(spec);

        // Calcular métricas globales para los contadores superiores de la UI
        List<Usuario> todosLosUsuarios = usuarioRepository.findAll();
        long total = todosLosUsuarios.size();

        long activos = todosLosUsuarios.stream()
                .filter(u -> u.getFechaUltimoLogin() != null && u.getFechaUltimoLogin().isAfter(limiteActivo))
                .count();

        long inactivos = total - activos;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<UsuarioReporteDTO> listaDto = usuariosFiltrados.stream().map(u -> {
            String prof = u.getProfesion() != null ? u.getProfesion().getNombreProfesion() : "Sin profesión";
            String fechaRegStr = u.getFechaRegistro() != null ? u.getFechaRegistro().format(formatter) : "";

            String estadoCalculado = (u.getFechaUltimoLogin() != null && u.getFechaUltimoLogin().isAfter(limiteActivo))
                    ? "ACTIVO" : "INACTIVO";

            return UsuarioReporteDTO.builder()
                    .id(u.getIdUsuario())
                    .nombreCompleto(u.getNombre())
                    .correo(u.getCorreo())
                    .profesion(prof)
                    .fechaRegistro(fechaRegStr)
                    .estado(estadoCalculado)
                    .build();
        }).toList();

        return ReporteUsuariosResponseDTO.builder()
                .totalUsuarios(total)
                .usuariosActivos(activos)
                .usuariosInactivos(inactivos)
                .usuarios(listaDto)
                .build();
    }
}