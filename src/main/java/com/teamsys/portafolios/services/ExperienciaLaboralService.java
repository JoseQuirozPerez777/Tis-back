package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.ExperienciaLaboralRequestDTO;
import com.teamsys.portafolios.dto.ExperienciaLaboralUpdateDTO;
import com.teamsys.portafolios.entities.ExperienciaLaboral;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.ExperienciaLaboralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ExperienciaLaboralService {

    @Autowired
    private ExperienciaLaboralRepository repository;

    public List<ExperienciaLaboral> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    @Transactional
    public ExperienciaLaboral guardar(ExperienciaLaboralRequestDTO dto, Usuario usuario) {
        validarFechas(dto);
        ExperienciaLaboral exp = new ExperienciaLaboral();
        return mapearYGuardar(exp, dto, usuario);
    }

    @Transactional
    public ExperienciaLaboral actualizar(ExperienciaLaboralUpdateDTO dto, Usuario usuario) {
        validarFechas(dto);
        ExperienciaLaboral exp = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Experiencia laboral no encontrada"));
        
        return mapearYGuardar(exp, dto, usuario);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private void validarFechas(ExperienciaLaboralRequestDTO dto) {
        if (dto.getFechaFin() != null && dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }

    private ExperienciaLaboral mapearYGuardar(ExperienciaLaboral entidad, ExperienciaLaboralRequestDTO dto, Usuario usuario) {
        entidad.setUsuario(usuario);
        entidad.setEmpresa(dto.getEmpresa());
        entidad.setCargo(dto.getCargo());
        entidad.setFechaInicio(dto.getFechaInicio());
        entidad.setFechaFin(dto.isEsTrabajoActual() ? null : dto.getFechaFin());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setEsTrabajoActual(dto.isEsTrabajoActual());
        return repository.save(entidad);
    }
}