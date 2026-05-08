package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.TecnologiaResponseDTO;
import com.teamsys.portafolios.entities.Tecnologia;
import com.teamsys.portafolios.repositories.TecnologiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TecnologiaService {

    @Autowired
    private TecnologiaRepository tecnologiaRepository;

    public List<TecnologiaResponseDTO> listarTodas() {
        return tecnologiaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<TecnologiaResponseDTO> listarPorCategoria(String categoria) {
        return tecnologiaRepository.findByCategoria(categoria).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private TecnologiaResponseDTO convertirADTO(Tecnologia t) {
        return new TecnologiaResponseDTO(
                t.getId(),
                t.getNombre(),
                t.getCategoria(),
                t.getLogoUrl()
        );
    }
}