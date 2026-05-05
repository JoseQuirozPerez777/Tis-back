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
        f.setFechaInicio(dto.getFechaInicio());
        f.setFechaFin(dto.isEnCurso() ? null : dto.getFechaFin());
        f.setDescripcion(dto.getDescripcion());
        f.setEnCurso(dto.isEnCurso());
        f.setUrlImagen(dto.getUrlImagen()); // Mapear nueva columna
    }

    private FormacionResponseDTO convertirADTO(FormacionAcademica f) {
        return new FormacionResponseDTO(
                f.getId(),
                f.getInstitucion(),
                f.getTituloObtenido(),
                f.getFechaInicio(),
                f.getFechaFin(),
                f.getDescripcion(),
                f.isEnCurso(),
                f.getUrlImagen() // Pasar al DTO de respuesta
        );
    }
}