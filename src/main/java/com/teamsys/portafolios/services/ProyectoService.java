package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.ProyectoRequestDTO;
import com.teamsys.portafolios.dto.ProyectoResponseDTO;
import com.teamsys.portafolios.entities.Proyecto;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    public List<ProyectoResponseDTO> obtenerProyectosPorUsuario(Usuario usuario) {
        return proyectoRepository.findByUsuario(usuario)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ProyectoResponseDTO agregarProyecto(ProyectoRequestDTO dto, Usuario usuario) {
        Proyecto proyecto = new Proyecto();
        actualizarEntidadDesdeDTO(proyecto, dto);
        proyecto.setUsuario(usuario);

        Proyecto guardado = proyectoRepository.save(proyecto);
        return convertirADTO(guardado);
    }

    public ProyectoResponseDTO actualizarProyecto(Long id, ProyectoRequestDTO dto, Usuario usuario) {
        Proyecto proyecto = proyectoRepository.findByIdProyectoAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado o no autorizado"));

        actualizarEntidadDesdeDTO(proyecto, dto);
        Proyecto actualizado = proyectoRepository.save(proyecto);
        return convertirADTO(actualizado);
    }

    public void eliminarProyecto(Long id, Usuario usuario) {
        Proyecto proyecto = proyectoRepository.findByIdProyectoAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado o no autorizado"));
        proyectoRepository.delete(proyecto);
    }

    private void actualizarEntidadDesdeDTO(Proyecto proyecto, ProyectoRequestDTO dto) {
        proyecto.setTitulo(dto.getTitulo());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setTecnologias(dto.getTecnologias());
        proyecto.setEnlaceGithub(dto.getEnlaceGithub());
        proyecto.setEnlaceDemo(dto.getEnlaceDemo());
        proyecto.setUrlImagen(dto.getUrlImagen());
        proyecto.setEsPublico(dto.isEsPublico());
    }

    private ProyectoResponseDTO convertirADTO(Proyecto p) {
        return new ProyectoResponseDTO(
                p.getIdProyecto(), p.getTitulo(), p.getDescripcion(),
                p.getTecnologias(), p.getEnlaceGithub(), p.getEnlaceDemo(),
                p.getUrlImagen(), p.isEsPublico()
        );
    }
}