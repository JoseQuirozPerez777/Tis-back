package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.ProyectoRequestDTO;
import com.teamsys.portafolios.dto.ProyectoResponseDTO;
import com.teamsys.portafolios.entities.Proyecto;
import com.teamsys.portafolios.entities.Tecnologia;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.ProyectoRepository;
import com.teamsys.portafolios.repositories.TecnologiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private TecnologiaRepository tecnologiaRepository;

    public List<ProyectoResponseDTO> obtenerProyectosPorUsuario(Usuario usuario) {
        return proyectoRepository.findByUsuario(usuario)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProyectoResponseDTO agregarProyecto(ProyectoRequestDTO dto, Usuario usuario) {
        // Procesamos IDs existentes y nombres nuevos
        List<Tecnologia> tecnologias = procesarTecnologias(dto);

        Proyecto proyecto = Proyecto.builder()
                .titulo(dto.getTitulo())
                .rolProyecto(dto.getRolProyecto())
                .descripcion(dto.getDescripcion())
                .urlsAdicionales(dto.getUrlsAdicionales())
                .urlsImagenes(dto.getUrlsImagenes())
                .tecnologias(tecnologias)
                .enlaceGithub(dto.getEnlaceGithub())
                .enlaceDemo(dto.getEnlaceDemo())
                .destacar(dto.isDestacar()) // Agregado
                .urlPdf(dto.getUrlPdf())     // Agregado
                .fechaInicio(dto.getFechaInicio())
                .fechaFinalizacion(dto.getFechaFinalizacion())
                .estadoProyecto(dto.getEstadoProyecto())
                .esPublico(dto.isEsPublico())
                .usuario(usuario)
                .build();

        Proyecto guardado = proyectoRepository.save(proyecto);
        return convertirADTO(guardado);
    }

    @Transactional
    public ProyectoResponseDTO actualizarProyecto(Long id, ProyectoRequestDTO dto, Usuario usuario) {
        Proyecto proyecto = proyectoRepository.findByIdProyectoAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado o no autorizado"));

        // Actualizamos los campos y procesamos las tecnologías
        actualizarEntidadDesdeDTO(proyecto, dto);

        Proyecto actualizado = proyectoRepository.save(proyecto);
        return convertirADTO(actualizado);
    }

    @Transactional
    public void eliminarProyecto(Long id, Usuario usuario) {
        Proyecto proyecto = proyectoRepository.findByIdProyectoAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado o no autorizado"));
        proyectoRepository.delete(proyecto);
    }

    /**
     * Lógica centralizada para manejar tecnologías por ID y por Nombre
     */
    private List<Tecnologia> procesarTecnologias(ProyectoRequestDTO dto) {
        // 1. Obtener las tecnologías por ID (las que ya existen en el catálogo)
        List<Tecnologia> tecnologias = (dto.getTecnologiaIds() != null)
                ? tecnologiaRepository.findAllById(dto.getTecnologiaIds())
                : new ArrayList<>();

        // 2. Procesar las "nuevasTecnologias" (Strings) enviadas opcionalmente
        if (dto.getNuevasTecnologias() != null && !dto.getNuevasTecnologias().isEmpty()) {
            for (String nombre : dto.getNuevasTecnologias()) {
                // Si la tecnología ya existe por nombre, la recuperamos; si no, la creamos.
                Tecnologia tec = tecnologiaRepository.findByNombre(nombre)
                        .orElseGet(() -> {
                            Tecnologia nueva = new Tecnologia();
                            nueva.setNombre(nombre);
                            nueva.setCategoria("Otros"); // Categoría por defecto
                            return tecnologiaRepository.save(nueva);
                        });

                // Evitamos duplicados en la lista del proyecto actual
                if (!tecnologias.contains(tec)) {
                    tecnologias.add(tec);
                }
            }
        }
        return tecnologias;
    }

    private void actualizarEntidadDesdeDTO(Proyecto proyecto, ProyectoRequestDTO dto) {
        // Importante: Usar la lógica de procesarTecnologias también aquí
        List<Tecnologia> tecnologias = procesarTecnologias(dto);

        proyecto.setTitulo(dto.getTitulo());
        proyecto.setRolProyecto(dto.getRolProyecto());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setUrlsAdicionales(dto.getUrlsAdicionales());
        proyecto.setUrlsImagenes(dto.getUrlsImagenes());
        proyecto.setTecnologias(tecnologias);
        proyecto.setEnlaceGithub(dto.getEnlaceGithub());
        proyecto.setEnlaceDemo(dto.getEnlaceDemo());
        proyecto.setDestacar(dto.isDestacar()); // Agregado
        proyecto.setUrlPdf(dto.getUrlPdf());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setFechaFinalizacion(dto.getFechaFinalizacion());
        proyecto.setEstadoProyecto(dto.getEstadoProyecto());
        proyecto.setEsPublico(dto.isEsPublico());
    }

    private ProyectoResponseDTO convertirADTO(Proyecto p) {
        return ProyectoResponseDTO.builder()
                .idProyecto(p.getIdProyecto())
                .titulo(p.getTitulo())
                .rolProyecto(p.getRolProyecto())
                .descripcion(p.getDescripcion())
                .urlsAdicionales(p.getUrlsAdicionales())
                .urlsImagenes(p.getUrlsImagenes())
                .tecnologiaIds(p.getTecnologias().stream()
                        .map(Tecnologia::getId)
                        .collect(Collectors.toList()))
                .enlaceGithub(p.getEnlaceGithub())
                .enlaceDemo(p.getEnlaceDemo())
                .destacar(p.isDestacar()) // Agregado
                .urlPdf(p.getUrlPdf())     // Agregado
                .fechaInicio(p.getFechaInicio())
                .fechaFinalizacion(p.getFechaFinalizacion())
                .estadoProyecto(p.getEstadoProyecto())
                .esPublico(p.isEsPublico())
                .idUsuario(p.getUsuario().getIdUsuario())
                .build();
    }
}