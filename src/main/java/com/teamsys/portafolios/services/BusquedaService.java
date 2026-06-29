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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> usuarioRoot = cq.from(Usuario.class);
        cq.distinct(true);

        List<Predicate> predicates = construirPredicados(filtros, cb, cq, usuarioRoot, correoUsuarioActual);
        cq.where(predicates.toArray(new Predicate[0]));

        aplicarOrdenamiento(filtros != null ? filtros.getOrdenarPor() : null, cb, cq, usuarioRoot);

        TypedQuery<Usuario> query = entityManager.createQuery(cq);

        int paginaActual = (filtros == null || filtros.getPagina() < 1) ? 1 : filtros.getPagina();
        int limiteMax = (filtros == null || filtros.getLimite() < 1) ? 10 : filtros.getLimite();
        int registroInicio = (paginaActual - 1) * limiteMax;

        query.setFirstResult(registroInicio);
        query.setMaxResults(limiteMax);

        List<Usuario> usuariosEncontrados = query.getResultList();

        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Usuario> countRoot = countCq.from(Usuario.class);
        countCq.distinct(true);

        List<Predicate> countPredicates = construirPredicados(filtros, cb, countCq, countRoot, correoUsuarioActual);
        countCq.select(cb.countDistinct(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        long totalResultados = entityManager.createQuery(countCq).getSingleResult();

        List<PortafolioResponseDTO> listaTarjetas = usuariosEncontrados.stream()
                .map(this::convertirAFormatoTarjeta)
                .collect(Collectors.toList());

        int totalPaginas = (int) Math.ceil((double) totalResultados / limiteMax);
        if (totalPaginas == 0) totalPaginas = 1;

        return new ResultadoBusquedaDTO(totalResultados, paginaActual, limiteMax, totalPaginas, listaTarjetas);
    }

    // =====================================================================
    // LOGICA DE FILTROS TOTALMENTE INTACTA (NO SE MODIFICA NADA AQUÍ)
    // =====================================================================
    private List<Predicate> construirPredicados(FiltrosDTO filtros, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Usuario> usuarioRoot, String correoUsuarioActual) {
        List<Predicate> predicates = new ArrayList<>();
        if (correoUsuarioActual != null) {
            predicates.add(cb.notEqual(usuarioRoot.get("correo"), correoUsuarioActual));
        }
        if (filtros == null) return predicates;

        if (filtros.getBuscar() != null && !filtros.getBuscar().isBlank()) {
            String patronBuscar = "%" + filtros.getBuscar().toLowerCase() + "%";
            Join<Usuario, Profesion> joinProf = usuarioRoot.join("profesion", JoinType.LEFT);
            Predicate matchNombre = cb.like(cb.lower(usuarioRoot.get("nombre")), patronBuscar);
            Predicate matchProfesion = cb.like(cb.lower(joinProf.get("nombreProfesion")), patronBuscar);
            Predicate matchDireccion = cb.like(cb.lower(usuarioRoot.get("direccion")), patronBuscar);
            predicates.add(cb.or(matchNombre, matchProfesion, matchDireccion));
        }

        agregarFiltrosExperiencia(filtros.getExperienciaLaboral(), cb, cq, usuarioRoot, predicates);
        agregarFiltrosHabilidadTecnica(filtros.getHabilidadTecnica(), cb, cq, usuarioRoot, predicates);
        agregarFiltrosHabilidadBlanda(filtros.getHabilidadBlanda(), cb, cq, usuarioRoot, predicates);
        agregarFiltrosProyecto(filtros.getProyecto(), cb, cq, usuarioRoot, predicates);
        agregarFiltrosFormacion(filtros.getFormacionAcademica(), cb, cq, usuarioRoot, predicates);
        return predicates;
    }

    private void agregarFiltrosExperiencia(FiltrosDTO.ExperienciaLaboralFiltro e, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Usuario> root, List<Predicate> predicates) {
        if (e == null) return;
        boolean tieneCriterio = (e.getNombreEmpresa() != null && !e.getNombreEmpresa().isBlank()) || (e.getCargo() != null && !e.getCargo().isBlank()) || (e.getCiudad() != null && !e.getCiudad().isBlank()) || (e.getAnosExperiencia() != null) || (e.getTecnologias() != null && !e.getTecnologias().isEmpty());
        if (!tieneCriterio) return;
        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<ExperienciaLaboral> subRoot = subquery.from(ExperienciaLaboral.class);
        subquery.select(subRoot.get("usuario").get("idUsuario"));
        List<Predicate> subPredicates = new ArrayList<>();
        subPredicates.add(cb.equal(subRoot.get("usuario"), root));
        if (e.getNombreEmpresa() != null && !e.getNombreEmpresa().isBlank()) subPredicates.add(cb.like(cb.lower(subRoot.get("nombreEmpresa")), "%" + e.getNombreEmpresa().toLowerCase() + "%"));
        if (e.getCargo() != null && !e.getCargo().isBlank()) subPredicates.add(cb.like(cb.lower(subRoot.get("cargoPuesto")), "%" + e.getCargo().toLowerCase() + "%"));
        if (e.getCiudad() != null && !e.getCiudad().isBlank()) subPredicates.add(cb.like(cb.lower(subRoot.get("ubicacion")), "%" + e.getCiudad().toLowerCase() + "%"));
        if (e.getAnosExperiencia() != null) {
            Expression<Integer> anoInicio = cb.function("YEAR", Integer.class, subRoot.get("fechaInicio"));
            Expression<Integer> anoFin = cb.selectCase().when(cb.isNull(subRoot.get("fechaFin")), cb.function("YEAR", Integer.class, cb.currentDate())).otherwise(cb.function("YEAR", Integer.class, subRoot.get("fechaFin"))).as(Integer.class);
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
        boolean tieneCriterio = (ht.getNombre() != null && !ht.getNombre().isBlank()) || (ht.getNivel() != null) || (ht.getAnosExperiencia() != null);
        if (!tieneCriterio) return;
        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<HabilidadTecnica> subRoot = subquery.from(HabilidadTecnica.class);
        subquery.select(subRoot.get("usuario").get("idUsuario"));
        List<Predicate> subPredicates = new ArrayList<>();
        subPredicates.add(cb.equal(subRoot.get("usuario"), root));
        if (ht.getNombre() != null && !ht.getNombre().isBlank()) subPredicates.add(cb.like(cb.lower(subRoot.get("nombre")), "%" + ht.getNombre().toLowerCase() + "%"));
        if (ht.getNivel() != null) subPredicates.add(cb.equal(subRoot.get("nivelDominio"), HabilidadTecnica.NivelDominio.valueOf(ht.getNivel().name())));
        if (ht.getAnosExperiencia() != null) subPredicates.add(cb.greaterThanOrEqualTo(subRoot.get("anosExperiencia"), ht.getAnosExperiencia()));
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
        boolean tieneCriterio = (pr.getNombre() != null && !pr.getNombre().isBlank()) || (pr.getRol() != null && !pr.getRol().isBlank()) || (pr.getTecnologias() != null && !pr.getTecnologias().isEmpty());
        if (!tieneCriterio) return;
        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Proyecto> subRoot = subquery.from(Proyecto.class);
        subquery.select(subRoot.get("usuario").get("idUsuario"));
        List<Predicate> subPredicates = new ArrayList<>();
        subPredicates.add(cb.equal(subRoot.get("usuario"), root));
        if (pr.getNombre() != null && !pr.getNombre().isBlank()) subPredicates.add(cb.like(cb.lower(subRoot.get("titulo")), "%" + pr.getNombre().toLowerCase() + "%"));
        if (pr.getRol() != null && !pr.getRol().isBlank()) subPredicates.add(cb.like(cb.lower(subRoot.get("rolProyecto")), "%" + pr.getRol().toLowerCase() + "%"));
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
        boolean tieneCriterio = (fa.getInstitucion() != null && !fa.getInstitucion().isBlank()) || (fa.getTitulo() != null && !fa.getTitulo().isBlank()) || (fa.getNivel() != null) || (fa.getEstado() != null);
        if (!tieneCriterio) return;
        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<FormacionAcademica> subRoot = subquery.from(FormacionAcademica.class);
        subquery.select(subRoot.get("usuario").get("idUsuario"));
        List<Predicate> subPredicates = new ArrayList<>();
        subPredicates.add(cb.equal(subRoot.get("usuario"), root));
        if (fa.getInstitucion() != null && !fa.getInstitucion().isBlank()) subPredicates.add(cb.like(cb.lower(subRoot.get("institucion")), "%" + fa.getInstitucion().toLowerCase() + "%"));
        if (fa.getTitulo() != null && !fa.getTitulo().isBlank()) subPredicates.add(cb.like(cb.lower(subRoot.get("tituloObtenido")), "%" + fa.getTitulo().toLowerCase() + "%"));
        if (fa.getNivel() != null) subPredicates.add(cb.equal(subRoot.get("nivel"), FormacionAcademica.NivelAcademico.valueOf(fa.getNivel().name())));
        if (fa.getEstado() != null) subPredicates.add(cb.equal(subRoot.get("estado"), FormacionAcademica.EstadoFormacion.valueOf(fa.getEstado().name())));
        subquery.where(subPredicates.toArray(new Predicate[0]));
        predicates.add(cb.exists(subquery));
    }

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

    // =====================================================================
    // ACTUALIZACIÓN DE MAPEO INTEGRANDO LA LÓGICA DE PORTAFOLIO SERVICE
    // =====================================================================
    private PortafolioResponseDTO convertirAFormatoTarjeta(Usuario usuario) {
        // 1. Obtener nombres de tecnologías únicos (Como en PortafolioService)
        List<HabilidadTecnica> habilidades = habilidadTecnicaRepository.findByUsuario(usuario);
        List<String> listaTecnologias = habilidades.stream()
                .map(HabilidadTecnica::getNombre)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        // 2. Obtener las experiencias para calcular los años totales exactos
        List<ExperienciaLaboral> experiencias = experienciaLaboralRepository.findByUsuario(usuario);
        int totalAniosExp = calcularAniosExperienciaTotal(experiencias);

        // 3. Contar la cantidad de proyectos que sean exclusivamente Públicos (Como en PortafolioService)
        List<Proyecto> proyectos = proyectoRepository.findByUsuario(usuario);
        int totalProyectosPublicos = (int) proyectos.stream()
                .filter(Proyecto::isEsPublico)
                .count();

        // 4. Extraer especialización y modalidad laboral de forma dinámica basada en sus experiencias
        String especializacionDinamica = experiencias.stream()
                .filter(exp -> exp.getEspecializacion() != null && !exp.getEspecializacion().isBlank())
                .map(ExperienciaLaboral::getEspecializacion)
                .findFirst()
                .orElse(null);

        String modalidadDinamica = experiencias.stream()
                .filter(exp -> exp.getModalidadTrabajo() != null)
                .map(exp -> exp.getModalidadTrabajo().name())
                .findFirst()
                .orElse(null);

        return PortafolioResponseDTO.builder()
                .id(usuario.getIdUsuario())
                .nombreCompleto(usuario.getNombre())
                .profesion(usuario.getProfesion() != null ? usuario.getProfesion().getNombreProfesion() : null)
                .ubicacion(usuario.getDireccion())
                .disponibilidad(usuario.getDisponibilidad() != null ? usuario.getDisponibilidad() : "")
                .fotoPerfilUrl(usuario.getFoto() != null ? usuario.getFoto() : "")
                .resumen(usuario.getBiografia() != null ? usuario.getBiografia() : "")
                
                // IGUAL QUE PORTAFOLIO SERVICE: Construcción dinámica de urlPublica
                .urlPublica("/portafolio/" + usuario.getIdUsuario()) 
                
                // Variables obtenidas mediante la misma lógica calculada
                .tecnologias(listaTecnologias)
                .experienciaAnios(totalAniosExp)
                .cantidadProyectos(totalProyectosPublicos)
                
                // Campos adicionales de tarjeta resueltos dinámicamente
                .especializacion(especializacionDinamica)
                .modalidadTrabajo(modalidadDinamica)
                .idiomas(new ArrayList<>())
                .build();
    }

    // IGUAL QUE PORTAFOLIO SERVICE: Método exacto copiado para calcular los años transcurridos
    private int calcularAniosExperienciaTotal(List<ExperienciaLaboral> experiencias) {
        long totalDias = 0;
        for (ExperienciaLaboral exp : experiencias) {
            LocalDate inicio = exp.getFechaInicio();
            LocalDate fin = exp.isActualmenteTrabajoAqui() || exp.getFechaFin() == null ?
                    LocalDate.now() : exp.getFechaFin();

            if (inicio != null) {
                totalDias += ChronoUnit.DAYS.between(inicio, fin);
            }
        }
        return (int) (totalDias / 365);
    }
}