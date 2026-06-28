package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.PortafolioCompletoDTO;
import com.teamsys.portafolios.entities.*;
import com.teamsys.portafolios.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamsys.portafolios.dto.BusquedaFiltrosDTO;
import com.teamsys.portafolios.dto.PortafolioResponseDTO;
import com.teamsys.portafolios.dto.ResultadoBusquedaDTO;
import com.teamsys.portafolios.dto.VisibilidadPerfilDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortafolioService {

    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ExperienciaLaboralRepository experienciaLaboralRepository;

    @Autowired
    private HabilidadBlandaRepository habilidadBlandaRepository;

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private HabilidadTecnicaRepository habilidadTecnicaRepository;
    
    @Autowired
    private VisibilidadPerfilService visibilidadPerfilService;

    private final ExperienciaLaboralRepository experienciaRepository;
    private final FormacionRepository formacionRepository;
    private final HabilidadTecnicaRepository habTecnicaRepository;
    private final HabilidadBlandaRepository habBlandaRepository;
    private final RedSocialRepository redSocialRepository;

    public PortafolioService(ExperienciaLaboralRepository experienciaRepository,
                             FormacionRepository formacionRepository,
                             HabilidadTecnicaRepository habTecnicaRepository,
                             HabilidadBlandaRepository habBlandaRepository,
                             ProyectoRepository proyectoRepository,
                             RedSocialRepository redSocialRepository) {
        this.experienciaRepository = experienciaRepository;
        this.formacionRepository = formacionRepository;
        this.habTecnicaRepository = habTecnicaRepository;
        this.habBlandaRepository = habBlandaRepository;
        this.proyectoRepository = proyectoRepository;
        this.redSocialRepository = redSocialRepository;
    }

    @Transactional(readOnly = true)
    public PortafolioCompletoDTO compilarPortafolio(Usuario usuario) {
        Long idUsuario = usuario.getIdUsuario();

        // 1. Mapear Experiencias Laborales (Extrayendo nombres de tecnologías)
        List<PortafolioCompletoDTO.ExperienciaLaboralResumenDTO> listaExperiencias = 
            experienciaRepository.findByUsuario(usuario).stream()
            .filter(ExperienciaLaboral::isEsPublico)
            .map(exp -> PortafolioCompletoDTO.ExperienciaLaboralResumenDTO.builder()
                    .nombreEmpresa(exp.getNombreEmpresa())
                    .cargoPuesto(exp.getCargoPuesto())
                    .areaProfesional(exp.getAreaProfesional())
                    .especializacion(exp.getEspecializacion())
                    .fechaInicio(exp.getFechaInicio().toString())
                    .fechaFin(exp.getFechaFin() != null ? exp.getFechaFin().toString() : null)
                    .actualmenteTrabajoAqui(exp.isActualmenteTrabajoAqui())
                    .modalidadTrabajo(exp.getModalidadTrabajo() != null ? exp.getModalidadTrabajo().name() : null)
                    .ubicacion(exp.getUbicacion())
                    .tipoContrato(exp.getTipoContrato() != null ? exp.getTipoContrato().name() : null)
                    .descripcionProyecto(exp.getDescripcionProyecto())
                    .evidenciaLaboralPdfUrl(exp.getEvidenciaLaboralPdfUrl())
                    .proyectoRelacionadoUrl(exp.getProyectoRelacionadoUrl())
                    .tecnologias(exp.getTecnologiasHerramientas().stream()
                            .map(Tecnologia::getNombre)
                            .collect(Collectors.toList()))
                    .build()
            ).collect(Collectors.toList());

        // 2. Mapear Formación Académica
        List<PortafolioCompletoDTO.FormacionAcademicaResumenDTO> listaFormaciones = 
            formacionRepository.findByUsuario(usuario).stream()
            .filter(FormacionAcademica::isEsPublico)
            .map(form -> PortafolioCompletoDTO.FormacionAcademicaResumenDTO.builder()
                    .institucion(form.getInstitucion())
                    .tituloObtenido(form.getTituloObtenido())
                    .nivel(form.getNivel() != null ? form.getNivel().name() : null)
                    .area(form.getArea())
                    .fechaInicio(form.getFechaInicio().toString())
                    .fechaFin(form.getFechaFin() != null ? form.getFechaFin().toString() : null)
                    .descripcion(form.getDescripcion())
                    .estado(form.getEstado() != null ? form.getEstado().name() : null)
                    .urlImagen(form.getUrlImagen())
                    .build()
            ).collect(Collectors.toList());

        // 3. Mapear Habilidades Técnicas (Extrayendo nombre de Categoria)
        List<PortafolioCompletoDTO.HabilidadTecnicaResumenDTO> listaHabTecnicas = 
            habTecnicaRepository.findByUsuario(usuario).stream()
            .filter(HabilidadTecnica::isEsPublico)
            .map(ht -> PortafolioCompletoDTO.HabilidadTecnicaResumenDTO.builder()
                    .nombre(ht.getNombre())
                    .categoria(ht.getCategoria() != null ? ht.getCategoria().getNombre() : null)
                    .nivelDominio(ht.getNivelDominio() != null ? ht.getNivelDominio().name() : null)
                    .anosExperiencia(ht.getAnosExperiencia())
                    .descripcion(ht.getDescripcion())
                    .certificadoUrl(ht.getCertificadoUrl())
                    .build()
            ).collect(Collectors.toList());

        // 4. Mapear Habilidades Blandas (Extrayendo nombre de Categoria)
        List<PortafolioCompletoDTO.HabilidadBlandaResumenDTO> listaHabBlandas = 
            habBlandaRepository.findByUsuario(usuario).stream()
            .filter(HabilidadBlanda::isEsPublico)
            .map(hb -> PortafolioCompletoDTO.HabilidadBlandaResumenDTO.builder()
                    .nombre(hb.getNombre())
                    .categoria(hb.getCategoria() != null ? hb.getCategoria().getNombre() : null)
                    .descripcion(hb.getDescripcion())
                    .evidenciaUrl(hb.getEvidenciaUrl())
                    .build()
            ).collect(Collectors.toList());

        // 5. Mapear Proyectos (Filtrando públicos por seguridad y extrayendo tecnologías)
        List<PortafolioCompletoDTO.ProyectoResumenDTO> listaProyectos = 
            proyectoRepository.findByUsuario(usuario).stream()
                .filter(Proyecto::isEsPublico)
                .map(proy -> PortafolioCompletoDTO.ProyectoResumenDTO.builder()
                    .titulo(proy.getTitulo())
                    .rolProyecto(proy.getRolProyecto())
                    .descripcion(proy.getDescripcion())
                    .urlsImagenes(proy.getUrlsImagenes())
                    .urlsAdicionales(proy.getUrlsAdicionales())
                    .tecnologias(proy.getTecnologias().stream()
                            .map(Tecnologia::getNombre)
                            .collect(Collectors.toList()))
                    .enlaceGithub(proy.getEnlaceGithub())
                    .enlaceDemo(proy.getEnlaceDemo())
                    .fechaInicio(proy.getFechaInicio())
                    .fechaFinalizacion(proy.getFechaFinalizacion())
                    .estadoProyecto(proy.getEstadoProyecto())
                    .urlPdfs( proy.getUrlPdfs())
                    .destacar( proy.isDestacar())
                    .build()
            ).collect(Collectors.toList());

        // 6. Mapear Redes Sociales (Filtrando las marcadas como públicas)
        List<PortafolioCompletoDTO.RedSocialResumenDTO> listaRedes = 
            redSocialRepository.findByUsuario(usuario).stream()
                .filter(RedSocial::isEsPublico)
                .map(red -> PortafolioCompletoDTO.RedSocialResumenDTO.builder()
                    .nombreRed(red.getNombreRed())
                    .urlPerfil(red.getUrlPerfil())
                    .build()
            ).collect(Collectors.toList());

        String urlCvOficial = curriculumRepository.findByUsuarioAndEsOficialTrue(usuario)
            .map(Curriculum::getUrlPdf) // Extrae la propiedad 'urlPdf' si el CV existe
            .orElse(null);

        VisibilidadPerfilDTO visibilidad =
        visibilidadPerfilService.obtenerVisibilidadConfig(usuario);
        // Compilar todo en el DTO raíz de respuesta
        return PortafolioCompletoDTO.builder()
        .nombre(
                visibilidad.isNombreUsr()
                        ? usuario.getNombre()
                        : "Usuario Privado"
        )
        .correo(
                visibilidad.isCorreoUsr()
                        ? usuario.getCorreo()
                        : null
        )
        .foto(usuario.getFoto())
        .profesion(
                visibilidad.isProfesionUsr()
                        ? (usuario.getProfesion() != null
                            ? usuario.getProfesion().getNombreProfesion()
                            : "Sin profesión especificada")
                        : null
        )
        .biografia(
                visibilidad.isBiografiaUsr()
                        ? usuario.getBiografia()
                        : null
        )
        .telefono(
                visibilidad.isTelefonoUsr()
                        ? usuario.getTelefono()
                        : null
        )
        .direccion(
                visibilidad.isDireccionUsr()
                        ? usuario.getDireccion()
                        : null
        )
        .enlacePublico(usuario.getEnlacePublico())
        .urlCv(urlCvOficial)
        .experienciasLaborales(listaExperiencias)
        .formacionesAcademica(listaFormaciones)
        .habilidadesTecnicas(listaHabTecnicas)
        .habilidadesBlandas(listaHabBlandas)
        .proyectos(listaProyectos)
        .redesSociales(listaRedes)
        .build();
    }

    

    public ResultadoBusquedaDTO buscarPortafoliosConFiltros(
        BusquedaFiltrosDTO filtros,
        String correoUsuarioActual
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 1. Consulta principal para los usuarios paginados
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> usuarioRoot = cq.from(Usuario.class);
        cq.distinct(true);

        List<Predicate> predicates = construirPredicados(filtros, cb, cq, usuarioRoot, correoUsuarioActual);
        cq.where(predicates.toArray(new Predicate[0]));

        aplicarOrdenamiento(filtros.getOrdenarPor(), cb, cq, usuarioRoot);

        TypedQuery<Usuario> query = entityManager.createQuery(cq);

        // Control de paginación robusto
        int paginaActual = (filtros.getPagina() < 1) ? 1 : filtros.getPagina();
        int limiteMax = (filtros.getLimite() < 1) ? 10 : filtros.getLimite();
        int registroInicio = (paginaActual - 1) * limiteMax;

        query.setFirstResult(registroInicio);
        query.setMaxResults(limiteMax);

        List<Usuario> usuariosEncontrados = query.getResultList();

        // 2. Consulta paralela de conteo (Count) para metadata de paginación
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Usuario> countRoot = countCq.from(Usuario.class);
        countCq.distinct(true);

        List<Predicate> countPredicates = construirPredicados(filtros, cb, countCq, countRoot, correoUsuarioActual);
        countCq.select(cb.countDistinct(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        long totalResultados = entityManager.createQuery(countCq).getSingleResult();

        // 3. Mapeo a lista de tarjetas (PortafolioResponseDTO)
        List<PortafolioResponseDTO> listaTarjetas = usuariosEncontrados.stream()
                .map(this::convertirAFormatoTarjeta)
                .collect(Collectors.toList());

        int totalPaginas = (int) Math.ceil((double) totalResultados / limiteMax);
        if (totalPaginas == 0) totalPaginas = 1;

        return new ResultadoBusquedaDTO(totalResultados, paginaActual, limiteMax, totalPaginas, listaTarjetas);
    }

    private List<Predicate> construirPredicados(
        BusquedaFiltrosDTO filtros,
        CriteriaBuilder cb,
        CriteriaQuery<?> cq,
        Root<Usuario> root,
        String correoUsuarioActual
        ) {
        List<Predicate> predicates = new ArrayList<>();

        if (correoUsuarioActual != null && !correoUsuarioActual.isBlank()) {
        predicates.add(cb.notEqual(root.get("correo"), correoUsuarioActual));
        }
        

        // --- FILTRO GENERAL (BUSCAR) ---
        if (filtros.getBuscar() != null && !filtros.getBuscar().trim().isEmpty()) {
            String buscarLike = "%" + filtros.getBuscar().trim().toLowerCase() + "%";
            Join<Usuario, Profesion> profesionJoin = root.join("profesion", JoinType.LEFT);

            Predicate nombreMatch = cb.like(cb.lower(root.get("nombre")), buscarLike);
Predicate biografiaMatch = cb.like(cb.lower(root.get("biografia")), buscarLike);
Predicate profesionMatch = cb.like(cb.lower(profesionJoin.get("nombreProfesion")), buscarLike);

Subquery<Long> subqueryEmpresaGeneral = cq.subquery(Long.class);
Root<ExperienciaLaboral> subqueryEmpresaGeneralRoot =
        subqueryEmpresaGeneral.from(ExperienciaLaboral.class);

subqueryEmpresaGeneral.select(
        subqueryEmpresaGeneralRoot.get("usuario").get("idUsuario")
).where(
        cb.like(
                cb.lower(subqueryEmpresaGeneralRoot.get("nombreEmpresa")),
                buscarLike
        )
);

Predicate empresaGeneralMatch = root.get("idUsuario").in(subqueryEmpresaGeneral);

predicates.add(cb.or(
        nombreMatch,
        biografiaMatch,
        profesionMatch,
        empresaGeneralMatch
));
        }

        // --- FILTRO POR PROFESIÓN ---
        if (filtros.getProfesion() != null && !filtros.getProfesion().trim().isEmpty()) {
            Join<Usuario, Profesion> profesionJoin = root.join("profesion", JoinType.INNER);
            predicates.add(cb.equal(cb.lower(profesionJoin.get("nombreProfesion")), filtros.getProfesion().trim().toLowerCase()));
        }

        // --- FILTRO POR ESPECIALIZACIÓN ---
        if (filtros.getEspecializacion() != null && !filtros.getEspecializacion().trim().isEmpty()) {
            Subquery<Long> subqueryExpEsp = cq.subquery(Long.class);
            Root<ExperienciaLaboral> subqueryExpEspRoot = subqueryExpEsp.from(ExperienciaLaboral.class);
            subqueryExpEsp.select(subqueryExpEspRoot.get("usuario").get("idUsuario"))
                    .where(cb.equal(cb.lower(subqueryExpEspRoot.get("especializacion")), filtros.getEspecializacion().trim().toLowerCase()));

            predicates.add(root.get("idUsuario").in(subqueryExpEsp));
        }

        // --- FILTRO POR EMPRESA / EXPERIENCIA LABORAL ---
if (filtros.getEmpresa() != null && !filtros.getEmpresa().trim().isEmpty()) {
    String empresaLike = "%" + filtros.getEmpresa().trim().toLowerCase() + "%";

    Subquery<Long> subqueryEmpresa = cq.subquery(Long.class);
    Root<ExperienciaLaboral> subqueryEmpresaRoot =
            subqueryEmpresa.from(ExperienciaLaboral.class);

    subqueryEmpresa.select(
            subqueryEmpresaRoot.get("usuario").get("idUsuario")
    ).where(
            cb.like(
                    cb.lower(subqueryEmpresaRoot.get("nombreEmpresa")),
                    empresaLike
            )
    );

    predicates.add(root.get("idUsuario").in(subqueryEmpresa));
}

        // --- FILTRO POR FORMACIÓN ACADÉMICA ---
        if (filtros.getFormacionAcademica() != null && !filtros.getFormacionAcademica().trim().isEmpty()) {
            String formacionLike = "%" + filtros.getFormacionAcademica().trim().toLowerCase() + "%";
            Subquery<Long> subqueryForm = cq.subquery(Long.class);
            Root<FormacionAcademica> subqueryFormRoot = subqueryForm.from(FormacionAcademica.class);

            subqueryForm.select(subqueryFormRoot.get("usuario").get("idUsuario"))
                    .where(cb.or(

                            cb.like(cb.lower(subqueryFormRoot.get("institucion")),formacionLike),
                            cb.like(cb.lower(subqueryFormRoot.get("institucion")), formacionLike),

                            cb.like(cb.lower(subqueryFormRoot.get("area")), formacionLike),
                            cb.like(cb.lower(subqueryFormRoot.get("tituloObtenido")), formacionLike),
                            cb.like(cb.lower(subqueryFormRoot.get("nivel").as(String.class)), formacionLike)
                    ));

            predicates.add(root.get("idUsuario").in(subqueryForm));
        }

        // --- FILTRO POR DISPONIBILIDAD (Mapeado a la entidad Usuario) ---
        if (filtros.getDisponibilidad() != null && !filtros.getDisponibilidad().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("disponibilidad")), filtros.getDisponibilidad().trim().toLowerCase()));
        }

        // --- FILTRO POR MODALIDAD DE TRABAJO ---
        if (filtros.getModalidadTrabajo() != null && !filtros.getModalidadTrabajo().trim().isEmpty()) {
            Subquery<Long> subqueryExpMod = cq.subquery(Long.class);
            Root<ExperienciaLaboral> subqueryExpModRoot = subqueryExpMod.from(ExperienciaLaboral.class);
            subqueryExpMod.select(subqueryExpModRoot.get("usuario").get("idUsuario"))
                    .where(cb.equal(cb.upper(subqueryExpModRoot.get("modalidadTrabajo").as(String.class)), filtros.getModalidadTrabajo().trim().toUpperCase()));

            predicates.add(root.get("idUsuario").in(subqueryExpMod));
        }

        // --- FILTRO POR EXPERIENCIA MÍNIMA (AÑOS) ---
        if (filtros.getExperienciaMinima() != null && filtros.getExperienciaMinima() > 0) {
            Subquery<Long> subqueryExpMin = cq.subquery(Long.class);
            Root<ExperienciaLaboral> subqueryExpMinRoot = subqueryExpMin.from(ExperienciaLaboral.class);

            // 1. Usamos el objeto Coalesce específico de CriteriaBuilder para evitar el conflicto de tipos
            CriteriaBuilder.Coalesce<Object> coalesceFechaFin = cb.coalesce();
            coalesceFechaFin.value(subqueryExpMinRoot.get("fechaFin"));
            coalesceFechaFin.value(cb.currentDate()); // La base de datos se encargará de la conversión implícita

            Expression<LocalDate> fechaInicioExp = subqueryExpMinRoot.get("fechaInicio");

            // 2. Calculamos la diferencia de días usando la función nativa/dialecto
            Expression<Long> diasEntre = cb.function("datediff", Long.class,
                    cb.literal("DAY"),
                    fechaInicioExp,
                    coalesceFechaFin
            );

            // 3. Agrupamos y filtramos con el HAVING
            subqueryExpMin.select(subqueryExpMinRoot.get("usuario").get("idUsuario"))
                    .groupBy(subqueryExpMinRoot.get("usuario").get("idUsuario"))
                    .having(cb.ge(cb.sum(diasEntre), (long) (filtros.getExperienciaMinima() * 365)));

            predicates.add(root.get("idUsuario").in(subqueryExpMin));
        }
        // --- FILTRO AVANZADO POR TECNOLOGÍA (MULTIFUENTE) ---
        if (filtros.getTecnologia() != null && !filtros.getTecnologia().trim().isEmpty()) {
            String tecBusqueda = filtros.getTecnologia().trim().toLowerCase();
            String tecBusquedaLike = "%" + tecBusqueda + "%";

            // Subquery A: Proyectos
            Subquery<Long> subqueryProyecto = cq.subquery(Long.class);
            Root<Proyecto> subqueryProyectoRoot = subqueryProyecto.from(Proyecto.class);
            Join<Proyecto, Tecnologia> tecnologiaJoin = subqueryProyectoRoot.join("tecnologias", JoinType.INNER);
            subqueryProyecto.select(subqueryProyectoRoot.get("usuario").get("idUsuario"))
                    .where(cb.equal(cb.lower(tecnologiaJoin.get("nombre")), tecBusqueda));

            // Subquery B: Experiencias Laborales
            Subquery<Long> subqueryExp = cq.subquery(Long.class);
            Root<ExperienciaLaboral> subqueryExpRoot = subqueryExp.from(ExperienciaLaboral.class);
            Join<ExperienciaLaboral, Tecnologia> tecnologiaExpJoin = subqueryExpRoot.join("tecnologiasHerramientas", JoinType.INNER);
            subqueryExp.select(subqueryExpRoot.get("usuario").get("idUsuario"))
                    .where(cb.equal(cb.lower(tecnologiaExpJoin.get("nombre")), tecBusqueda));

            // Subquery C: Habilidades Técnicas
            Subquery<Long> subqueryHabTecnica = cq.subquery(Long.class);
            Root<HabilidadTecnica> subqueryHabRoot = subqueryHabTecnica.from(HabilidadTecnica.class);
            subqueryHabTecnica.select(subqueryHabRoot.get("usuario").get("idUsuario"))
                    .where(cb.equal(cb.lower(subqueryHabRoot.get("nombre")), tecBusqueda));

            // Subquery D: Formación Académica (Descripciones)
            Subquery<Long> subqueryFormacion = cq.subquery(Long.class);
            Root<FormacionAcademica> subqueryFormRoot = subqueryFormacion.from(FormacionAcademica.class);
            subqueryFormacion.select(subqueryFormRoot.get("usuario").get("idUsuario"))
                    .where(cb.or(
                            cb.like(cb.lower(subqueryFormRoot.get("descripcion")), tecBusquedaLike),
                            cb.like(cb.lower(subqueryFormRoot.get("tituloObtenido")), tecBusquedaLike)
                    ));

            predicates.add(cb.or(
                    root.get("idUsuario").in(subqueryProyecto),
                    root.get("idUsuario").in(subqueryExp),
                    root.get("idUsuario").in(subqueryHabTecnica),
                    root.get("idUsuario").in(subqueryFormacion)
            ));
        }

        // --- FILTRO DINÁMICO POR IDIOMAS ---
        if (filtros.getIdiomas() != null && !filtros.getIdiomas().isEmpty()) {
            for (String idioma : filtros.getIdiomas()) {
                if (idioma != null && !idioma.trim().isEmpty()) {
                    Subquery<Long> subqueryIdioma = cq.subquery(Long.class);
                    Root<HabilidadBlanda> subRoot = subqueryIdioma.from(HabilidadBlanda.class);
                    Join<HabilidadBlanda, Categoria> catJoin = subRoot.join("categoria", JoinType.INNER);

                    subqueryIdioma.select(subRoot.get("usuario").get("idUsuario"))
                            .where(cb.and(
                                    cb.equal(cb.lower(catJoin.get("nombre")), "idiomas"),
                                    cb.equal(cb.lower(subRoot.get("nombre")), idioma.trim().toLowerCase())
                            ));

                    predicates.add(root.get("idUsuario").in(subqueryIdioma));
                }
            }
        }

        // --- FILTRO POR UBICACIÓN ---
        if (filtros.getUbicacion() != null && !filtros.getUbicacion().trim().isEmpty()) {
            String ubicacionClean = "%" + filtros.getUbicacion().trim().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(root.get("direccion")), ubicacionClean));
        }

        return predicates;
    }

    private void aplicarOrdenamiento(String ordenarPor, CriteriaBuilder cb, CriteriaQuery<Usuario> cq, Root<Usuario> root) {
        if (ordenarPor == null || ordenarPor.trim().isEmpty()) {
            cq.orderBy(cb.desc(root.get("fechaRegistro")));
            return;
        }

        switch (ordenarPor.toLowerCase().trim()) {
            case "recientes":
                cq.orderBy(cb.desc(root.get("fechaRegistro")));
                break;
            case "experiencia":
                cq.orderBy(cb.asc(root.get("nombre")));
                break;
            case "relevancia":
            default:
                cq.orderBy(cb.asc(root.get("nombre")));
                break;
        }
    }

    private PortafolioResponseDTO convertirAFormatoTarjeta(Usuario usuario) {
        List<Proyecto> proyectos = proyectoRepository.findByUsuario(usuario);
        List<ExperienciaLaboral> experiencias = experienciaLaboralRepository.findByUsuario(usuario);
        List<HabilidadBlanda> habilidadesBlandas = habilidadBlandaRepository.findByUsuario(usuario);
        List<HabilidadTecnica> habilidadesTecnicas = habilidadTecnicaRepository.findByUsuario(usuario);

        List<String> idiomasReales = habilidadesBlandas.stream()
                .filter(hb -> hb.getCategoria() != null && "idiomas".equalsIgnoreCase(hb.getCategoria().getNombre()))
                .map(HabilidadBlanda::getNombre)
                .collect(Collectors.toList());

        if (idiomasReales.isEmpty()) {
            idiomasReales = Collections.singletonList("Español");
        }

        int totalAnios = calcularAniosExperienciaTotal(experiencias);

        Set<String> tecnologiasUnicas = new CaseInsensitiveSet();

        proyectos.stream()
                .flatMap(p -> p.getTecnologias().stream())
                .map(Tecnologia::getNombre)
                .filter(Objects::nonNull)
                .forEach(tecnologiasUnicas::add);

        experiencias.stream()
                .filter(e -> e.getTecnologiasHerramientas() != null)
                .flatMap(e -> e.getTecnologiasHerramientas().stream())
                .map(Tecnologia::getNombre)
                .filter(Objects::nonNull)
                .forEach(tecnologiasUnicas::add);

        habilidadesTecnicas.stream()
                .map(HabilidadTecnica::getNombre)
                .filter(Objects::nonNull)
                .forEach(tecnologiasUnicas::add);

        String modalidad = experiencias.isEmpty() ? "No especificada" :
                experiencias.get(0).getModalidadTrabajo().toString();

        String especialidad = experiencias.isEmpty() ? "General" :
                (experiencias.get(0).getEspecializacion() != null ? experiencias.get(0).getEspecializacion() : "General");

        // Lee directamente el nuevo campo nativo del usuario
        String disponibilidadEstado = (usuario.getDisponibilidad() != null) ? usuario.getDisponibilidad() : "Disponible";

        return PortafolioResponseDTO.builder()
                .id(usuario.getIdUsuario())
                .nombreCompleto(usuario.getNombre())
                .profesion(usuario.getProfesion() != null ? usuario.getProfesion().getNombreProfesion() : "Sin Profesión")
                .especializacion(especialidad)
                .ubicacion(usuario.getDireccion() != null ? usuario.getDireccion() : "No especificada")
                .disponibilidad(disponibilidadEstado)
                .modalidadTrabajo(modalidad)
                .tecnologias(new ArrayList<>(tecnologiasUnicas))
                .idiomas(idiomasReales)
                .experienciaAnios(totalAnios)
                .cantidadProyectos((int) proyectos.stream().filter(Proyecto::isEsPublico).count())
                .fotoPerfilUrl(usuario.getFoto() != null ? usuario.getFoto() : "")
                .urlPublica("/portafolio/" + usuario.getIdUsuario())
                .resumen(usuario.getBiografia() != null ? usuario.getBiografia() : "")
                .build();
    }

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

    private static class CaseInsensitiveSet extends AbstractSet<String> {
        private final Map<String, String> map = new LinkedHashMap<>();

        @Override
        public boolean add(String s) {
            String lower = s.toLowerCase().trim();
            if (map.containsKey(lower)) {
                return false;
            }
            map.put(lower, s);
            return true;
        }

        @Override
        public Iterator<String> iterator() {
            return map.values().iterator();
        }

        @Override
        public int size() {
            return map.size();
        }
    }
}