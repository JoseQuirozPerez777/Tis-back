package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.EnlacePublicoDTO;
import com.teamsys.portafolios.dto.PortafolioCompletoDTO;
import com.teamsys.portafolios.dto.UsuarioPublicoDTO;
import com.teamsys.portafolios.entities.Curriculum;
import com.teamsys.portafolios.entities.ExperienciaLaboral;
import com.teamsys.portafolios.entities.FormacionAcademica;
import com.teamsys.portafolios.entities.HabilidadBlanda;
import com.teamsys.portafolios.entities.HabilidadTecnica;
import com.teamsys.portafolios.entities.Proyecto;
import com.teamsys.portafolios.entities.RedSocial;
import com.teamsys.portafolios.entities.Tecnologia;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.CategoriaRepository;
import com.teamsys.portafolios.repositories.CurriculumRepository;
import com.teamsys.portafolios.repositories.ExperienciaLaboralRepository;
import com.teamsys.portafolios.repositories.FormacionRepository;
import com.teamsys.portafolios.repositories.HabilidadBlandaRepository;
import com.teamsys.portafolios.repositories.HabilidadTecnicaRepository;
import com.teamsys.portafolios.repositories.ProyectoRepository;
import com.teamsys.portafolios.repositories.RedSocialRepository;
import com.teamsys.portafolios.repositories.TecnologiaRepository;
import com.teamsys.portafolios.repositories.UsuarioRepository;

import lombok.Builder;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Base64;
import java.util.List;
import java.util.Collections; // CORRECCIÓN 1: Importación de Collections agregada

@Service
public class EnlacePublicoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private RedSocialRepository redSocialRepository;
    
    @Autowired
    private HabilidadBlandaRepository habilidadBlandaRepository;

    @Autowired
    private HabilidadTecnicaRepository habilidadTecnicaRepository;

    @Autowired
    private ExperienciaLaboralRepository experienciaLaboralRepository;

    @Autowired
    private FormacionRepository formacionRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private CurriculumRepository curriculumRepository;

    
    @Autowired
    private TecnologiaRepository tecnologiaRepository;

    @Value("${URL_FRONT}")
    private String dominioPagina;

    // 1. GENERAR: Crea la URL codificando el correo de forma reversible
    public EnlacePublicoDTO generarEnlace(String nombre, String correo) {
        // Codificamos el correo en Base64 y limpiamos caracteres no válidos para URL (como el '=')
        String codigoReversible = Base64.getUrlEncoder().withoutPadding().encodeToString(correo.toLowerCase().trim().getBytes());
        
        // Si el Base64 queda muy largo, para que parezca una URL real tomamos una sección segura, 
        // pero para asegurar que recuperes todo el correo, usamos el string completo en formato URL-safe.
        String nombreAmigable = transformarANombreAmigable(nombre);

        // La URL terminará con el código que representa fielmente al correo decodificable
        String urlFinal = dominioPagina + "/profile/" + nombreAmigable + "-" + codigoReversible;

        return new EnlacePublicoDTO(urlFinal);
    }

    private String obtenerCorreo(String urlOTextoCompleto){
        String codigo = urlOTextoCompleto;
        
        // 2. DECODIFICACIÓN: Convertimos el código de nuevo a texto plano (correo)
        byte[] decodedBytes = Base64.getUrlDecoder().decode(codigo);
        String correoDecodificado = new String(decodedBytes).trim();

        return correoDecodificado;
    }

    // CORRECCIÓN: Se corrigieron las variables cruzadas y tipos devueltos según PortafolioCompletoDTO
    public List<PortafolioCompletoDTO.ExperienciaLaboralResumenDTO> obtenerExperiencias(String urlOTextoCompleto) {
        try {
            String correoDecodificado = obtenerCorreo(urlOTextoCompleto);
            Usuario usuario = usuarioRepository.findByCorreo(correoDecodificado)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo decodificado"));

            List<ExperienciaLaboral> experiencias = experienciaLaboralRepository.findByUsuario(usuario);
            

            return experiencias.stream()
                    .filter(ExperienciaLaboral::isEsPublico) 
                    .map(exp -> PortafolioCompletoDTO.ExperienciaLaboralResumenDTO.builder()
                            .nombreEmpresa(exp.getNombreEmpresa())
                            .cargoPuesto(exp.getCargoPuesto())
                            .areaProfesional(exp.getAreaProfesional())
                            .especializacion(exp.getEspecializacion())
                            .fechaInicio(exp.getFechaInicio() != null ? exp.getFechaInicio().toString() : null)
                            .fechaFin(exp.getFechaFin() != null ? exp.getFechaFin().toString() : null)
                            .actualmenteTrabajoAqui(exp.isActualmenteTrabajoAqui())
                            .modalidadTrabajo(exp.getModalidadTrabajo() != null ? exp.getModalidadTrabajo().name() : null)
                            .ubicacion(exp.getUbicacion())
                            .tipoContrato(exp.getTipoContrato() != null ? exp.getTipoContrato().name() : null)
                            .descripcionProyecto(exp.getDescripcionProyecto())
                            .evidenciaLaboralPdfUrl(exp.getEvidenciaLaboralPdfUrl())
                            .proyectoRelacionadoUrl(exp.getProyectoRelacionadoUrl())
                            .tecnologias(exp.getTecnologiasHerramientas() != null ? 
                                    exp.getTecnologiasHerramientas().stream().map(Tecnologia::getNombre).toList() : Collections.emptyList())
                            .build())
                    .toList();

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El código de la URL no tiene un formato Base64 válido.");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener experiencias laborales: " + e.getMessage());
        }
    }

    // AGREGADO: Obtener Proyectos mapeados
    public List<PortafolioCompletoDTO.ProyectoResumenDTO> obtenerProyectos(String urlOTextoCompleto) {
        try {
            String correoDecodificado = obtenerCorreo(urlOTextoCompleto);
            Usuario usuario = usuarioRepository.findByCorreo(correoDecodificado)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo decodificado"));

            List<Proyecto> proyectos = proyectoRepository.findByUsuario(usuario);

            return proyectos.stream()
                    .filter(Proyecto::isEsPublico) // Filtrar solo los proyectos públicos
                    .map(pro -> PortafolioCompletoDTO.ProyectoResumenDTO.builder()
                            .titulo(pro.getTitulo())
                            .rolProyecto(pro.getRolProyecto())
                            .descripcion(pro.getDescripcion())
                            .urlsImagenes(pro.getUrlsImagenes())
                            .urlsAdicionales(pro.getUrlsAdicionales())
                            .enlaceGithub(pro.getEnlaceGithub())
                            .enlaceDemo(pro.getEnlaceDemo())
                            .fechaInicio(pro.getFechaInicio())
                            .fechaFinalizacion(pro.getFechaFinalizacion())
                            .estadoProyecto(pro.getEstadoProyecto())
                            .tecnologias(pro.getTecnologias() != null ? 
                                    pro.getTecnologias().stream().map(Tecnologia::getNombre).toList() : Collections.emptyList())
                            .build())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener proyectos: " + e.getMessage());
        }
    }

    // AGREGADO: Obtener Habilidades Técnicas mapeadas
    public List<PortafolioCompletoDTO.HabilidadTecnicaResumenDTO> obtenerHabilidadesTecnicas(String urlOTextoCompleto) {
        try {
            String correoDecodificado = obtenerCorreo(urlOTextoCompleto);
            Usuario usuario = usuarioRepository.findByCorreo(correoDecodificado)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo decodificado"));

            List<HabilidadTecnica> habTecnicas = habilidadTecnicaRepository.findByUsuario(usuario);

            return habTecnicas.stream()
                    .filter(HabilidadTecnica::isEsPublico)
                    .map(hab -> PortafolioCompletoDTO.HabilidadTecnicaResumenDTO.builder()
                            .nombre(hab.getNombre())
                            .categoria(hab.getCategoria() != null ? hab.getCategoria().getNombre() : "Sin Categoría")
                            .nivelDominio(hab.getNivelDominio() != null ? hab.getNivelDominio().name() : null)
                            .anosExperiencia(hab.getAnosExperiencia())
                            .descripcion(hab.getDescripcion())
                            .certificadoUrl(hab.getCertificadoUrl())
                            .build())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener habilidades técnicas: " + e.getMessage());
        }
    }

    // AGREGADO: Obtener Habilidades Blandas mapeadas
    public List<PortafolioCompletoDTO.HabilidadBlandaResumenDTO> obtenerHabilidadesBlandas(String urlOTextoCompleto) {
        try {
            String correoDecodificado = obtenerCorreo(urlOTextoCompleto);
            Usuario usuario = usuarioRepository.findByCorreo(correoDecodificado)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo decodificado"));

            List<HabilidadBlanda> habBlandas = habilidadBlandaRepository.findByUsuario(usuario);
            //poner el filter si es publico en todos
            return habBlandas.stream()
                    .filter(HabilidadBlanda::isEsPublico)
                    .map(hab -> PortafolioCompletoDTO.HabilidadBlandaResumenDTO.builder()
                            .nombre(hab.getNombre())
                            .categoria(hab.getCategoria() != null ? hab.getCategoria().getNombre() : "Sin Categoría")
                            .descripcion(hab.getDescripcion())
                            .evidenciaUrl(hab.getEvidenciaUrl())
                            .build())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener habilidades blandas: " + e.getMessage());
        }
    }

    // AGREGADO: Obtener Formación Académica mapeada
    public List<PortafolioCompletoDTO.FormacionAcademicaResumenDTO> obtenerFormacionesAcademicas(String urlOTextoCompleto) {
        try {
            String correoDecodificado = obtenerCorreo(urlOTextoCompleto);
            Usuario usuario = usuarioRepository.findByCorreo(correoDecodificado)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo decodificado"));

            List<FormacionAcademica> formaciones = formacionRepository.findByUsuario(usuario);

            return formaciones.stream()
                    .map(form -> PortafolioCompletoDTO.FormacionAcademicaResumenDTO.builder()
                            .institucion(form.getInstitucion())
                            .tituloObtenido(form.getTituloObtenido())
                            .nivel(form.getNivel() != null ? form.getNivel().name() : null)
                            .area(form.getArea())
                            .fechaInicio(form.getFechaInicio() != null ? form.getFechaInicio().toString() : null)
                            .fechaFin(form.getFechaFin() != null ? form.getFechaFin().toString() : null)
                            .descripcion(form.getDescripcion())
                            .estado(form.getEstado() != null ? form.getEstado().name() : null)
                            .urlImagen(form.getUrlImagen())
                            .build())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener formación académica: " + e.getMessage());
        }
    }

    // 2. DECODIFICAR Y OBTENER DATOS: El Front te manda la URL completa o el slug, extraemos el código, lo decodificamos para obtener el correo y buscamos el usuario
    
    public UsuarioPublicoDTO obtenerPerfilPorUrlMapeada(String urlOTextoCompleto) {
    try {
        
        String correoDecodificado = obtenerCorreo(urlOTextoCompleto);

        // 3. Buscar al usuario en la base de datos
        Usuario usuario = usuarioRepository.findByCorreo(correoDecodificado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo decodificado"));

        // 4. Mapear datos básicos al DTO
        UsuarioPublicoDTO dto = new UsuarioPublicoDTO();
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setFoto(usuario.getFoto());
        dto.setBiografia(usuario.getBiografia());
        dto.setTelefono(usuario.getTelefono());
        dto.setDireccion(usuario.getDireccion());

        // 5. Mapear la profesión
        if (usuario.getProfesion() != null) {
            dto.setNombreProfesion(usuario.getProfesion().getNombreProfesion());
        } else {
            dto.setNombreProfesion("Profesión no especificada");
        }

        // 6. SOLUCIÓN: Mapear y asignar la lista de redes sociales
        List<RedSocial> redes = redSocialRepository.findByUsuario(usuario); 
        if (redes != null && !redes.isEmpty()) {
            List<UsuarioPublicoDTO.RedSocialResumenDTO> redesDTO = redes.stream()
                .map(red -> UsuarioPublicoDTO.RedSocialResumenDTO.builder()
                    .nombreRed(red.getNombreRed()) // Ajusta al método real de tu entidad RedSocial
                    .urlPerfil(red.getUrlPerfil()) // Ajusta al método real de tu entidad RedSocial
                    .build())
                .toList(); // En Java 16+, o .collect(Collectors.toList()) en versiones anteriores
            
            dto.setRedes(redesDTO);
        }

        return dto;
        
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("El código de la URL no tiene un formato Base64 válido.");
    } catch (Exception e) {
        throw new RuntimeException("Error al intentar decodificar el enlace del perfil: " + e.getMessage());
    }
}

    private String transformarANombreAmigable(String nombre) {
        if (nombre == null) return "usuario";
        String limpio = Normalizer.normalize(nombre, Normalizer.Form.NFD);
        limpio = limpio.replaceAll("\\p{M}", "");
        return limpio.toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9\\-]", "");
    }

    // AGREGADO: Obtener el Currículum Oficial del usuario (esOficial == true) mapeado al DTO
    public CurriculumResumenDTO obtenerCurriculumOficial(String urlOTextoCompleto) {
        try {
            // 1. Extraer y decodificar el correo desde la URL
            String correoDecodificado = obtenerCorreo(urlOTextoCompleto);

            // 2. Buscar al usuario en la base de datos
            Usuario usuario = usuarioRepository.findByCorreo(correoDecodificado)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo decodificado"));

            // 3. Buscar la lista de currículums asociados al usuario
            List<Curriculum> curriculums = curriculumRepository.findByUsuario(usuario);
            if (curriculums == null || curriculums.isEmpty()) {
                return null;
            }

            // 4. Filtrar por el que tiene la bandera 'esOficial == true' y mapearlo al DTO
            return curriculums.stream()
                    .filter(Curriculum::isEsOficial)
                    .map(cv -> CurriculumResumenDTO.builder()
                            .titulo(cv.getTitulo())
                            .urlCv(cv.getUrlPdf()) // Mapea urlPdf de la entidad a urlCv del DTO
                            .build())
                    .findFirst()
                    .orElse(null); // Si el usuario tiene CVs pero ninguno es oficial, retorna null

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El código de la URL no tiene un formato Base64 válido.");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el currículum oficial: " + e.getMessage());
        }
    }

    @Data @Builder
    public static class CurriculumResumenDTO {
        private String titulo;
        private String urlCv;
    }
}