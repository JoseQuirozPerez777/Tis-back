package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.ExperienciaLaboralRequestDTO;
import com.teamsys.portafolios.dto.ExperienciaLaboralResponseDTO;
import com.teamsys.portafolios.entities.ExperienciaLaboral;
import com.teamsys.portafolios.entities.Tecnologia;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.ExperienciaLaboralRepository;
import com.teamsys.portafolios.repositories.TecnologiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienciaLaboralService {

    @Autowired
    private ExperienciaLaboralRepository repository;

    @Autowired
    private TecnologiaRepository tecnologiaRepository;

    public List<ExperienciaLaboralResponseDTO> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExperienciaLaboralResponseDTO guardar(ExperienciaLaboralRequestDTO dto, Usuario usuario) {
        validarFechas(dto);
        ExperienciaLaboral exp = new ExperienciaLaboral();
        exp.setUsuario(usuario);
        mapearEntidad(exp, dto);
        return convertirADTO(repository.save(exp));
    }

    @Transactional
    public ExperienciaLaboralResponseDTO actualizar(Long id, ExperienciaLaboralRequestDTO dto, Usuario usuario) {
        validarFechas(dto);
        ExperienciaLaboral exp = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experiencia laboral no encontrada"));

        // Verificación de seguridad: asegurar que la experiencia pertenece al usuario
        if (!exp.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para editar esta experiencia");
        }

        mapearEntidad(exp, dto);
        return convertirADTO(repository.save(exp));
    }

    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private void validarFechas(ExperienciaLaboralRequestDTO dto) {
        if (dto.getFechaFin() != null && dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }

    private void mapearEntidad(ExperienciaLaboral entidad, ExperienciaLaboralRequestDTO dto) {
        entidad.setNombreEmpresa(dto.getNombreEmpresa());
        entidad.setCargoPuesto(dto.getCargoPuesto());
        entidad.setAreaProfesional(dto.getAreaProfesional());
        entidad.setEspecializacion(dto.getEspecializacion());
        entidad.setFechaInicio(dto.getFechaInicio());
        entidad.setFechaFin(dto.isActualmenteTrabajoAqui() ? null : dto.getFechaFin());
        entidad.setActualmenteTrabajoAqui(dto.isActualmenteTrabajoAqui());
        entidad.setUbicacion(dto.getUbicacion());
        entidad.setDescripcionProyecto(dto.getDescripcionProyecto());
        entidad.setEvidenciaLaboralPdfUrl(dto.getEvidenciaLaboralPdfUrl());
        entidad.setProyectoRelacionadoUrl(dto.getProyectoRelacionadoUrl());

        // Mapeo de Enums desde String
        if (dto.getModalidadTrabajo() != null) {
            entidad.setModalidadTrabajo(ExperienciaLaboral.ModalidadTrabajo.valueOf(dto.getModalidadTrabajo().toUpperCase()));
        }
        if (dto.getTipoContrato() != null) {
            entidad.setTipoContrato(ExperienciaLaboral.TipoContrato.valueOf(dto.getTipoContrato().toUpperCase()));
        }

        // Mapeo de Tecnologías por ID
        if (dto.getTecnologiasIds() != null) {
            List<Tecnologia> tecnologias = tecnologiaRepository.findAllById(dto.getTecnologiasIds());
            entidad.setTecnologiasHerramientas(tecnologias);
        }
    }

    private ExperienciaLaboralResponseDTO convertirADTO(ExperienciaLaboral exp) {
        ExperienciaLaboralResponseDTO dto = new ExperienciaLaboralResponseDTO();
        dto.setId(exp.getId());
        dto.setNombreEmpresa(exp.getNombreEmpresa());
        dto.setCargoPuesto(exp.getCargoPuesto());
        dto.setAreaProfesional(exp.getAreaProfesional());
        dto.setEspecializacion(exp.getEspecializacion());
        dto.setFechaInicio(exp.getFechaInicio());
        dto.setFechaFin(exp.getFechaFin());
        dto.setActualmenteTrabajoAqui(exp.isActualmenteTrabajoAqui());
        dto.setUbicacion(exp.getUbicacion());
        dto.setDescripcionProyecto(exp.getDescripcionProyecto());
        dto.setEvidenciaLaboralPdfUrl(exp.getEvidenciaLaboralPdfUrl());
        dto.setProyectoRelacionadoUrl(exp.getProyectoRelacionadoUrl());

        if (exp.getModalidadTrabajo() != null) dto.setModalidadTrabajo(exp.getModalidadTrabajo().name());
        if (exp.getTipoContrato() != null) dto.setTipoContrato(exp.getTipoContrato().name());

        // Convertir la lista de objetos Tecnologia a lista de Strings (nombres) para el Frontend
        if (exp.getTecnologiasHerramientas() != null) {
            dto.setNombresTecnologias(exp.getTecnologiasHerramientas().stream()
                    .map(Tecnologia::getNombre)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}