package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.CurriculumDTO;
import com.teamsys.portafolios.dto.CurriculumRequestDTO;
import com.teamsys.portafolios.entities.Curriculum;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.CurriculumRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;

    public CurriculumService(CurriculumRepository curriculumRepository) {
        this.curriculumRepository = curriculumRepository;
    }

    @Transactional(readOnly = true)
    public List<CurriculumDTO> listarCvsDelUsuario(Long idUsuario) {
        return curriculumRepository.findByUsuarioIdUsuarioOrderByFechaCreacionDesc(idUsuario)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CurriculumDTO guardarCurriculum(CurriculumRequestDTO dto, Usuario usuario) {
        // Regla estricta de unicidad para el valor TRUE
        if (Boolean.TRUE.equals(dto.getEsOficial())) {
            desactivarOficialesAnteriores(usuario.getIdUsuario());
        }

        Curriculum cv = new Curriculum();
        cv.setTitulo(dto.getTitulo());
        cv.setUrlPdf(dto.getUrlPdf());
        cv.setTipo(dto.getTipo().toUpperCase());
        cv.setEsOficial(dto.getEsOficial());
        cv.setFechaCreacion(LocalDateTime.now());
        cv.setUsuario(usuario);

        return convertirADto(curriculumRepository.save(cv));
    }

    @Transactional
    public CurriculumDTO actualizarCurriculum(Long idCurriculum, CurriculumRequestDTO dto, Long idUsuario) {
        Curriculum cv = curriculumRepository.findById(idCurriculum)
                .orElseThrow(() -> new RuntimeException("Currículum no encontrado"));

        if (!cv.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("Acceso denegado.");
        }

        // Si el cliente edita este CV y decide cambiar su estado a oficial (true)
        if (Boolean.TRUE.equals(dto.getEsOficial())) {
            desactivarOficialesAnteriores(idUsuario);
        }

        cv.setTitulo(dto.getTitulo());
        cv.setEsOficial(dto.getEsOficial());

        return convertirADto(curriculumRepository.save(cv));
    }

    @Transactional
    public void eliminarCurriculum(Long idCurriculum, Long idUsuario) {
        Curriculum cv = curriculumRepository.findById(idCurriculum)
                .orElseThrow(() -> new RuntimeException("Currículum no encontrado"));

        if (!cv.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("Acceso denegado.");
        }

        curriculumRepository.delete(cv);
    }

    // Garantiza de manera consistente la desactivación por lotes en la base de datos
    private void desactivarOficialesAnteriores(Long idUsuario) {
        List<Curriculum> listaCvs = curriculumRepository.findByUsuarioIdUsuarioOrderByFechaCreacionDesc(idUsuario);
        boolean huboCambios = false;
        
        for (Curriculum cv : listaCvs) {
            if (cv.isEsOficial()) {
                cv.setEsOficial(false);
                huboCambios = true;
            }
        }
        
        if (huboCambios) {
            curriculumRepository.saveAll(listaCvs);
        }
    }

    private CurriculumDTO convertirADto(Curriculum cv) {
        return new CurriculumDTO(
                cv.getIdCurriculum(),
                cv.getTitulo(),
                cv.getUrlPdf(),
                cv.getFechaCreacion(),
                cv.getTipo(),
                cv.isEsOficial()
        );
    }
}