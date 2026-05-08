package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.FormacionRequestDTO;
import com.teamsys.portafolios.dto.FormacionResponseDTO;
import com.teamsys.portafolios.entities.FormacionAcademica;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.FormacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormacionService {

    @Autowired
    private FormacionRepository formacionRepository;

    public List<FormacionResponseDTO> obtenerPorUsuario(Usuario usuario) {
        return formacionRepository.findByUsuario(usuario).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public FormacionResponseDTO agregar(FormacionRequestDTO dto, Usuario usuario) {
        FormacionAcademica f = new FormacionAcademica();
        mapearEntidad(f, dto);
        f.setUsuario(usuario);
        return convertirADTO(formacionRepository.save(f));
    }

    public FormacionResponseDTO actualizar(Long id, FormacionRequestDTO dto, Usuario usuario) {
        FormacionAcademica f = formacionRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Formación no encontrada"));
        mapearEntidad(f, dto);
        return convertirADTO(formacionRepository.save(f));
    }

    public void eliminar(Long id, Usuario usuario) {
        FormacionAcademica f = formacionRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Formación no encontrada"));
        formacionRepository.delete(f);
    }

    private void mapearEntidad(FormacionAcademica f, FormacionRequestDTO dto) {
        f.setInstitucion(dto.getInstitucion());
        f.setTituloObtenido(dto.getTituloObtenido());
        f.setArea(dto.getArea());
        f.setFechaInicio(dto.getFechaInicio());
        f.setFechaFin(dto.getFechaFin());
        f.setDescripcion(dto.getDescripcion());
        f.setUrlImagen(dto.getUrlImagen());

        // Conversión de String a Enum (Nivel)
        if (dto.getNivel() != null) {
            f.setNivel(FormacionAcademica.NivelAcademico.valueOf(dto.getNivel().toUpperCase()));
        }

        // Conversión de String a Enum (Estado)
        if (dto.getEstado() != null) {
            f.setEstado(FormacionAcademica.EstadoFormacion.valueOf(dto.getEstado().toUpperCase()));
        }
    }

    private FormacionResponseDTO convertirADTO(FormacionAcademica f) {
        FormacionResponseDTO dto = new FormacionResponseDTO();
        dto.setId(f.getId());
        dto.setInstitucion(f.getInstitucion());
        dto.setTituloObtenido(f.getTituloObtenido());
        dto.setArea(f.getArea());
        dto.setFechaInicio(f.getFechaInicio());
        dto.setFechaFin(f.getFechaFin());
        dto.setDescripcion(f.getDescripcion());
        dto.setUrlImagen(f.getUrlImagen());

        // Conversión de Enum a String para el DTO
        if (f.getNivel() != null) {
            dto.setNivel(f.getNivel().name());
        }

        if (f.getEstado() != null) {
            dto.setEstado(f.getEstado().name());
        }

        return dto;
    }
}