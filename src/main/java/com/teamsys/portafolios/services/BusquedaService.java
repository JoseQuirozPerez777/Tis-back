package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.FiltrosDTO;
import com.teamsys.portafolios.dto.PortafolioResponseDTO;
import com.teamsys.portafolios.dto.ResultadoBusquedaDTO;
import com.teamsys.portafolios.entities.*;
import com.teamsys.portafolios.repositories.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusquedaService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ExperienciaLaboralRepository experienciaLaboralRepository;

    @Autowired
    private HabilidadBlandaRepository habilidadBlandaRepository;

    @Autowired
    private HabilidadTecnicaRepository habilidadTecnicaRepository;
    
    @Autowired
    private VisibilidadPerfilRepository visibilidadPerfilRepository;

    @Autowired
    private FormacionRepository formacionRepository;

    @Transactional(readOnly = true)
    public ResultadoBusquedaDTO buscarPortafoliosConFiltros(FiltrosDTO filtros, String correoUsuarioActual) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 1. Consulta principal para obtener los usuarios paginados
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> usuarioRoot = cq.from(Usuario.class);
        cq.distinct(true);

        List<Predicate> predicates = construirPredicados(filtros, cb, cq, usuarioRoot, correoUsuarioActual);
        cq.where(predicates.toArray(new Predicate[0]));

        aplicarOrdenamiento(filtros != null ? filtros.getOrdenarPor() : null, cb, cq, usuarioRoot);

        TypedQuery<Usuario> query = entityManager.createQuery(cq);

        // Control estricto de paginación
        int paginaActual = (filtros == null || filtros.getPagina() < 1) ? 1 : filtros.getPagina();
        int limiteMax = (filtros == null || filtros.getLimite() < 1) ? 10 : filtros.getLimite();
        int registroInicio = (paginaActual - 1) * limiteMax;

        query.setFirstResult(registroInicio);
        query.setMaxResults(limiteMax);

        List<Usuario> usuariosEncontrados = query.getResultList();

        // 2. Consulta paralela (Count) para calcular la metadata de paginación
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Usuario> countRoot = countCq.from(Usuario.class);
        countCq.distinct(true);

        List<Predicate> countPredicates = construirPredicados(filtros, cb, countCq, countRoot, correoUsuarioActual);
        countCq.select(cb.countDistinct(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        long totalResultados = entityManager.createQuery(countCq).getSingleResult();

        // 3. Mapeo del dominio a PortafolioResponseDTO utilizando los campos correctos
        List<PortafolioResponseDTO> listaTarjetas = usuariosEncontrados.stream()
                .map(this::convertirAFormatoTarjeta)
                .collect(Collectors.toList());

        int totalPaginas = (int) Math.ceil((double) totalResultados / limiteMax);
        if (totalPaginas == 0) totalPaginas = 1;

        return new ResultadoBusquedaDTO(totalResultados, paginaActual, limiteMax, totalPaginas, listaTarjetas);
    }

    // =====================================================================
    // MÓDULO CENTRAL DE CONSTRUCCIÓN DE PREDICADOS
    // =====================================================================
    private List<Predicate> construirPredicados(
            FiltrosDTO filtros, 
            CriteriaBuilder cb, 
            CriteriaQuery<?> cq, 
            Root<Usuario> usuarioRoot, 
            String correoUsuarioActual
    ) {
        List<Predicate> predicates = new ArrayList<>();

        // Seguridad base: Evitar que el usuario logueado se visualice en sus propias búsquedas
        if (correoUsuarioActual != null) {
            predicates.add(cb.notEqual(usuarioRoot.get("correo"), correoUsuarioActual));
        }

        if (filtros == null) return predicates;

        // Filtro Global "buscar" (Aplica OR entre Nombre, Profesión y Dirección)
        if (filtros.getBuscar() != null && !filtros.getBuscar().isBlank()) {
            String patronBuscar = "%" + filtros.getBuscar().toLowerCase() + "%";
            Join<Usuario, Profesion> joinProf = usuarioRoot.join("profesion", JoinType.LEFT);
            
            Predicate matchNombre = cb.like(cb.lower(usuarioRoot.get("nombre")), patronBuscar);
            Predicate matchProfesion = cb.like(cb.lower(joinProf.get("nombreProfesion")), patronBuscar);
            Predicate matchDireccion = cb.like(cb.lower(usuarioRoot.get("direccion")), patronBuscar);
            
            predicates.add(cb.or(matchNombre, matchProfesion, matchDireccion));
        }

        // Distribución de filtros basados en Subconsultas independientes
        agregarFiltrosExperiencia(filtros.getExperienciaLaboral(), cb, cq, usuarioRoot, predicates);
        agregarFiltrosHabilidadTecnica(filtros.getHabilidadTecnica(), cb, cq, usuarioRoot, predicates);
        agregarFiltrosHabilidadBlanda(filtros.getHabilidadBlanda(), cb, cq, usuarioRoot, predicates);
        agregarFiltrosProyecto(filtros.getProyecto(), cb, cq, usuarioRoot, predicates);
        agregarFiltrosFormacion(filtros.getFormacionAcademica(), cb, cq, usuarioRoot, predicates);

        return predicates;
    }

    // =====================================================================
    // FILTROS CON SUBCONSULTAS (SUBQUERIES) AUTÓNOMAS
    // =====================================================================

    private void agregarFiltrosExperiencia(FiltrosDTO.ExperienciaLaboralFiltro e, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Usuario> root, List<Predicate> predicates) {
        if (e == null) return;
        
        boolean tieneCriterio = (e.getNombreEmpresa() != null && !e.getNombreEmpresa().isBlank()) ||
                                (e.getCargo() != null && !e.getCargo().isBlank()) ||
                                (e.getCiudad() != null && !e.getCiudad().isBlank()) ||
                                (e.getAnosExperiencia() != null) ||
                                (e.getTecnologias() != null && !e.getTecnologias().isEmpty());
                                
        if (!tieneCriterio) return;

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<ExperienciaLaboral> subRoot = subquery.from(ExperienciaLaboral.class);
        subquery.select(subRoot.get("usuario").get("idUsuario"));

        List<Predicate> subPredicates = new ArrayList<>();
        subPredicates.add(cb.equal(subRoot.get("usuario"), root));

        if (e.getNombreEmpresa() != null && !e.getNombreEmpresa().isBlank()) {
            subPredicates.add(cb.like(cb.lower(subRoot.get("nombreEmpresa")), "%" + e.getNombreEmpresa().toLowerCase() + "%"));
        }
        if (e.getCargo() != null && !e.getCargo().isBlank()) {
            subPredicates.add(cb.like(cb.lower(subRoot.get("cargoPuesto")), "%" + e.getCargo().toLowerCase() + "%"));
        }
        if (e.getCiudad() != null && !e.getCiudad().isBlank()) {
            subPredicates.add(cb.like(cb.lower(subRoot.get("ubicacion")), "%" + e.getCiudad().toLowerCase() + "%"));
        }
        if (e.getAnosExperiencia() != null) {
            Expression<Integer> anoInicio = cb.function("YEAR", Integer.class, subRoot.get("fechaInicio"));
            Expression<Integer> anoFin = cb.selectCase()
                .when(cb.isNull(subRoot.get("fechaFin")), cb.function("YEAR", Integer.class, cb.currentDate()))
                .otherwise(cb.function("YEAR", Integer.class, subRoot.get("fechaFin")))
                .as(Integer.class);
            Expression<Integer> totalAnos = cb.diff(anoFin, anoInicio);
            subPredicates.add(cb.greaterThanOrEqualTo(totalAnos, e.getAnosExperiencia()));
        }
        if (e.getTecnologias() != null && !e.getTecnologias().isEmpty()) {
            Join<ExperienciaLaboral, Tecnologia> joinTec = subRoot.join("tecnologiasHerramientas", JoinType.LEFT);
            List<String> tecs = e.getTecnologias().stream().map(String::toLowerCase).collect(Collectors.toList());
            subPredicates.add(cb.lower(joinTec.get("nombre")).in(tecs));
        }

        subquery.where(subPredicates.toArray(new Predicate[0]));
        predicates.add(cb.exists(subquery));
    }

    private void agregarFiltrosHabilidadTecnica(FiltrosDTO.HabilidadTecnicaFiltro ht, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Usuario> root, List<Predicate> predicates) {
        if (ht == null) return;
        
        boolean tieneCriterio = (ht.getNombre() != null && !ht.getNombre().isBlank()) ||
                                (ht.getNivel() != null) ||
                                (ht.getAnosExperiencia() != null);
                                
        if (!tieneCriterio) return;

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<HabilidadTecnica> subRoot = subquery.from(HabilidadTecnica.class);
        subquery.select(subRoot.get("usuario").get("idUsuario"));

        List<Predicate> subPredicates = new ArrayList<>();
        subPredicates.add(cb.equal(subRoot.get("usuario"), root));

        if (ht.getNombre() != null && !ht.getNombre().isBlank()) {
            subPredicates.add(cb.like(cb.lower(subRoot.get("nombre")), "%" + ht.getNombre().toLowerCase() + "%"));
        }
        if (ht.getNivel() != null) {
            HabilidadTecnica.NivelDominio nivelEntidad = HabilidadTecnica.NivelDominio.valueOf(ht.getNivel().name());
            subPredicates.add(cb.equal(subRoot.get("nivelDominio"), nivelEntidad));
        }
        if (ht.getAnosExperiencia() != null) {
            subPredicates.add(cb.greaterThanOrEqualTo(subRoot.get("anosExperiencia"), ht.getAnosExperiencia()));
        }

        subquery.where(subPredicates.toArray(new Predicate[0]));
        predicates.add(cb.exists(subquery));
    }

    private void agregarFiltrosHabilidadBlanda(FiltrosDTO.HabilidadBlandaFiltro hb, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Usuario> root, List<Predicate> predicates) {
        if (hb == null || hb.getNombre() == null || hb.getNombre().isBlank()) return;
        
        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<HabilidadBlanda> subRoot = subquery.from(HabilidadBlanda.class);
        subquery.select(subRoot.get("usuario").get("idUsuario"));

        List<Predicate> subPredicates = new ArrayList<>();
        subPredicates.add(cb.equal(subRoot.get("usuario"), root));
        subPredicates.add(cb.like(cb.lower(subRoot.get("nombre")), "%" + hb.getNombre().toLowerCase() + "%"));

        subquery.where(subPredicates.toArray(new Predicate[0]));
        predicates.add(cb.exists(subquery));
    }

    private void agregarFiltrosProyecto(FiltrosDTO.ProyectoFiltro pr, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Usuario> root, List<Predicate> predicates) {
        if (pr == null) return;
        
        boolean tieneCriterio = (pr.getNombre() != null && !pr.getNombre().isBlank()) ||
                                (pr.getRol() != null && !pr.getRol().isBlank()) ||
                                (pr.getTecnologias() != null && !pr.getTecnologias().isEmpty());
                                
        if (!tieneCriterio) return;

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Proyecto> subRoot = subquery.from(Proyecto.class);
        subquery.select(subRoot.get("usuario").get("idUsuario"));

        List<Predicate> subPredicates = new ArrayList<>();
        subPredicates.add(cb.equal(subRoot.get("usuario"), root));

        if (pr.getNombre() != null && !pr.getNombre().isBlank()) {
            subPredicates.add(cb.like(cb.lower(subRoot.get("titulo")), "%" + pr.getNombre().toLowerCase() + "%"));
        }
        if (pr.getRol() != null && !pr.getRol().isBlank()) {
            subPredicates.add(cb.like(cb.lower(subRoot.get("rolProyecto")), "%" + pr.getRol().toLowerCase() + "%"));
        }
        if (pr.getTecnologias() != null && !pr.getTecnologias().isEmpty()) {
            Join<Proyecto, Tecnologia> joinTecProy = subRoot.join("tecnologias", JoinType.LEFT);
            List<String> tecs = pr.getTecnologias().stream().map(String::toLowerCase).collect(Collectors.toList());
            subPredicates.add(cb.lower(joinTecProy.get("nombre")).in(tecs));
        }

        subquery.where(subPredicates.toArray(new Predicate[0]));
        predicates.add(cb.exists(subquery));
    }

    private void agregarFiltrosFormacion(FiltrosDTO.FormacionAcademicaFiltro fa, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Usuario> root, List<Predicate> predicates) {
        if (fa == null) return;
        
        boolean tieneCriterio = (fa.getInstitucion() != null && !fa.getInstitucion().isBlank()) ||
                                (fa.getTitulo() != null && !fa.getTitulo().isBlank()) ||
                                (fa.getNivel() != null) ||
                                (fa.getEstado() != null);
                                
        if (!tieneCriterio) return;

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<FormacionAcademica> subRoot = subquery.from(FormacionAcademica.class);
        subquery.select(subRoot.get("usuario").get("idUsuario"));

        List<Predicate> subPredicates = new ArrayList<>();
        subPredicates.add(cb.equal(subRoot.get("usuario"), root));

        if (fa.getInstitucion() != null && !fa.getInstitucion().isBlank()) {
            subPredicates.add(cb.like(cb.lower(subRoot.get("institucion")), "%" + fa.getInstitucion().toLowerCase() + "%"));
        }
        if (fa.getTitulo() != null && !fa.getTitulo().isBlank()) {
            subPredicates.add(cb.like(cb.lower(subRoot.get("tituloObtenido")), "%" + fa.getTitulo().toLowerCase() + "%"));
        }
        if (fa.getNivel() != null) {
            FormacionAcademica.NivelAcademico nivelEntidad = FormacionAcademica.NivelAcademico.valueOf(fa.getNivel().name());
            subPredicates.add(cb.equal(subRoot.get("nivel"), nivelEntidad));
        }
        if (fa.getEstado() != null) {
            FormacionAcademica.EstadoFormacion estadoEntidad = FormacionAcademica.EstadoFormacion.valueOf(fa.getEstado().name());
            subPredicates.add(cb.equal(subRoot.get("estado"), estadoEntidad));
        }

        subquery.where(subPredicates.toArray(new Predicate[0]));
        predicates.add(cb.exists(subquery));
    }

    // =====================================================================
    // UTILIDADES DE SOPORTE (ORDENAMIENTO Y MAPEOS)
    // =====================================================================

    private void aplicarOrdenamiento(String ordenarPor, CriteriaBuilder cb, CriteriaQuery<Usuario> cq, Root<Usuario> usuarioRoot) {
        if (ordenarPor == null) {
            cq.orderBy(cb.desc(usuarioRoot.get("idUsuario")));
            return;
        }
        switch (ordenarPor.toLowerCase()) {
            case "experiencia":
                cq.orderBy(cb.desc(usuarioRoot.get("fechaRegistro")));
                break;
            case "recientes":
            default:
                cq.orderBy(cb.desc(usuarioRoot.get("idUsuario")));
                break;
        }
    }

    private PortafolioResponseDTO convertirAFormatoTarjeta(Usuario usuario) {
        return PortafolioResponseDTO.builder()
                .id(usuario.getIdUsuario())
                .nombreCompleto(usuario.getNombre())
                // CORRECCIÓN AQUÍ: Cambiado usuario.getProfession() por usuario.getProfesion()
                .profesion(usuario.getProfesion() != null ? usuario.getProfesion().getNombreProfesion() : null)
                .especializacion(null) 
                .ubicacion(usuario.getDireccion())
                .disponibilidad(usuario.getDisponibilidad())
                .modalidadTrabajo(null)
                .tecnologias(new ArrayList<>()) 
                .idiomas(new ArrayList<>())
                .experienciaAnios(0)
                .cantidadProyectos(0)
                .fotoPerfilUrl(usuario.getFoto())
                .urlPublica(usuario.getEnlacePublico())
                .resumen(usuario.getBiografia())
                .build();
    }
}