package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.ProyectoRequestDTO;
import com.teamsys.portafolios.dto.ProyectoResponseDTO;
import com.teamsys.portafolios.entities.Proyecto;
import com.teamsys.portafolios.entities.Tecnologia;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.ProyectoRepository;
import com.teamsys.portafolios.repositories.TecnologiaRepository; // Necesario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private TecnologiaRepository tecnologiaRepository; // Inyectado para buscar tecnologías

    public List<ProyectoResponseDTO> obtenerProyectosPorUsuario(Usuario usuario) {
        return proyectoRepository.findByUsuario(usuario)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProyectoResponseDTO agregarProyecto(ProyectoRequestDTO dto, Usuario usuario) {
        // 1. Buscamos las tecnologías por los IDs enviados en el DTO
        List<Tecnologia> tecnologias = tecnologiaRepository.findAllById(dto.getTecnologiaIds());

        Proyecto proyecto = Proyecto.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .tecnologias(tecnologias) // Ahora sí coincide: List<Tecnologia>
                .enlaceGithub(dto.getEnlaceGithub())
                .enlaceDemo(dto.getEnlaceDemo())
                .urlsImagenes(dto.getUrlsImagenes())
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

    private void actualizarEntidadDesdeDTO(Proyecto proyecto, ProyectoRequestDTO dto) {
        // 1. Convertimos IDs a objetos Tecnología
        List<Tecnologia> tecnologias = tecnologiaRepository.findAllById(dto.getTecnologiaIds());

        proyecto.setTitulo(dto.getTitulo());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setTecnologias(tecnologias); // Seteamos la lista de objetos
        proyecto.setEnlaceGithub(dto.getEnlaceGithub());
        proyecto.setEnlaceDemo(dto.getEnlaceDemo());
        proyecto.setUrlsImagenes(dto.getUrlsImagenes());
        proyecto.setEsPublico(dto.isEsPublico());
    }

    private ProyectoResponseDTO convertirADTO(Proyecto p) {
        return ProyectoResponseDTO.builder()
                .idProyecto(p.getIdProyecto())
                .titulo(p.getTitulo())
                .descripcion(p.getDescripcion())
                // 2. Extraemos solo los IDs de las tecnologías para el DTO
                .tecnologiaIds(p.getTecnologias().stream()
                        .map(Tecnologia::getId)
                        .collect(Collectors.toList()))
                .enlaceGithub(p.getEnlaceGithub())
                .enlaceDemo(p.getEnlaceDemo())
                .urlsImagenes(p.getUrlsImagenes())
                .esPublico(p.isEsPublico())
                .idUsuario(p.getUsuario().getIdUsuario())
                .build();
    }
}