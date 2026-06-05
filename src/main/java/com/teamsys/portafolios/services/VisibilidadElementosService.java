package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.CambioVisibilidadDTO;
import com.teamsys.portafolios.dto.ElementoVisibilidadDTO;
import com.teamsys.portafolios.dto.PortafolioVisibilidadElementosDTO;
import com.teamsys.portafolios.dto.VisibilidadElementosMasivaDTO;
import com.teamsys.portafolios.entities.*;
import com.teamsys.portafolios.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VisibilidadElementosService {

    @Autowired
    private ExperienciaLaboralRepository experienciaRepository;

    @Autowired
    private FormacionRepository formacionRepository;

    @Autowired
    private HabilidadTecnicaRepository habTecnicaRepository;

    @Autowired
    private HabilidadBlandaRepository habBlandaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private RedSocialRepository redSocialRepository;

    /**
     * Guarda la visibilidad de múltiples elementos de todas las secciones en un solo bloque.
     */
    @Transactional
    public void guardarVisibilidadMasiva(VisibilidadElementosMasivaDTO dtoGlobal) {
        if (dtoGlobal.getExperiencias() != null) {
            actualizarVisibilidadExperiencias(dtoGlobal.getExperiencias());
        }
        if (dtoGlobal.getFormaciones() != null) {
            actualizarVisibilidadFormaciones(dtoGlobal.getFormaciones());
        }
        if (dtoGlobal.getHabilidadesTecnicas() != null) {
            actualizarVisibilidadHabilidadesTecnicas(dtoGlobal.getHabilidadesTecnicas());
        }
        if (dtoGlobal.getHabilidadesBlandas() != null) {
            actualizarVisibilidadHabilidadesBlandas(dtoGlobal.getHabilidadesBlandas());
        }
        if (dtoGlobal.getProyectos() != null) {
            actualizarVisibilidadProyectos(dtoGlobal.getProyectos());
        }
        if (dtoGlobal.getRedesSociales() != null) {
            actualizarVisibilidadRedesSociales(dtoGlobal.getRedesSociales());
        }
    }

    public void actualizarVisibilidadExperiencias(List<CambioVisibilidadDTO> dtos) {
        for (CambioVisibilidadDTO dto : dtos) {
            experienciaRepository.findById(dto.getId()).ifPresent(entidad -> {
                entidad.setEsPublico(dto.isEsPublico());
                experienciaRepository.save(entidad);
            });
        }
    }

    public void actualizarVisibilidadFormaciones(List<CambioVisibilidadDTO> dtos) {
        for (CambioVisibilidadDTO dto : dtos) {
            formacionRepository.findById(dto.getId()).ifPresent(entidad -> {
                entidad.setEsPublico(dto.isEsPublico());
                formacionRepository.save(entidad);
            });
        }
    }

    public void actualizarVisibilidadHabilidadesTecnicas(List<CambioVisibilidadDTO> dtos) {
        for (CambioVisibilidadDTO dto : dtos) {
            habTecnicaRepository.findById(dto.getId()).ifPresent(entidad -> {
                entidad.setEsPublico(dto.isEsPublico());
                habTecnicaRepository.save(entidad);
            });
        }
    }

    public void actualizarVisibilidadHabilidadesBlandas(List<CambioVisibilidadDTO> dtos) {
        for (CambioVisibilidadDTO dto : dtos) {
            habBlandaRepository.findById(dto.getId()).ifPresent(entidad -> {
                entidad.setEsPublico(dto.isEsPublico());
                habBlandaRepository.save(entidad);
            });
        }
    }

    public void actualizarVisibilidadProyectos(List<CambioVisibilidadDTO> dtos) {
        for (CambioVisibilidadDTO dto : dtos) {
            proyectoRepository.findById(dto.getId()).ifPresent(entidad -> {
                entidad.setEsPublico(dto.isEsPublico());
                proyectoRepository.save(entidad);
            });
        }
    }

    public void actualizarVisibilidadRedesSociales(List<CambioVisibilidadDTO> dtos) {
        for (CambioVisibilidadDTO dto : dtos) {
            redSocialRepository.findById(dto.getId()).ifPresent(entidad -> {
                entidad.setEsPublico(dto.isEsPublico());
                redSocialRepository.save(entidad);
            });
        }
    }

    /**
     * Obtiene el estado de visibilidad simplificado de todos los elementos del usuario.
     */
    @Transactional(readOnly = true)
    public PortafolioVisibilidadElementosDTO obtenerVisibilidadElementos(Usuario usuario) {

        List<ElementoVisibilidadDTO> experiencias = experienciaRepository.findByUsuario(usuario).stream()
                .map(e -> ElementoVisibilidadDTO.builder()
                        .id(e.getId())
                        .nombre(e.getNombreEmpresa() + " - " + e.getCargoPuesto())
                        .esPublico(e.isEsPublico())
                        .build())
                .toList();

        List<ElementoVisibilidadDTO> formaciones = formacionRepository.findByUsuario(usuario).stream()
                .map(f -> ElementoVisibilidadDTO.builder()
                        .id(f.getId())
                        .nombre(f.getInstitucion() + " (" + f.getTituloObtenido() + ")")
                        .esPublico(f.isEsPublico())
                        .build())
                .toList();

        List<ElementoVisibilidadDTO> habTecnicas = habTecnicaRepository.findByUsuario(usuario).stream()
                .map(ht -> ElementoVisibilidadDTO.builder()
                        .id(ht.getId())
                        .nombre(ht.getNombre())
                        .esPublico(ht.isEsPublico())
                        .build())
                .toList();

        List<ElementoVisibilidadDTO> habBlandas = habBlandaRepository.findByUsuario(usuario).stream()
                .map(hb -> ElementoVisibilidadDTO.builder()
                        .id(hb.getId())
                        .nombre(hb.getNombre())
                        .esPublico(hb.isEsPublico())
                        .build())
                .toList();

        List<ElementoVisibilidadDTO> proyectos = proyectoRepository.findByUsuario(usuario).stream()
                .map(p -> ElementoVisibilidadDTO.builder()
                        .id(p.getIdProyecto()) // Usa tu idProyecto original
                        .nombre(p.getTitulo())
                        .esPublico(p.isEsPublico())
                        .build())
                .toList();

        List<ElementoVisibilidadDTO> redes = redSocialRepository.findByUsuario(usuario).stream()
                .map(r -> ElementoVisibilidadDTO.builder()
                        .id(r.getIdRed()) // Usa tu idRed original
                        .nombre(r.getNombreRed())
                        .esPublico(r.isEsPublico())
                        .build())
                .toList();

        return PortafolioVisibilidadElementosDTO.builder()
                .experiencias(experiencias)
                .formaciones(formaciones)
                .habilidadesTecnicas(habTecnicas)
                .habilidadesBlandas(habBlandas)
                .proyectos(proyectos)
                .redesSociales(redes)
                .build();
    }
}