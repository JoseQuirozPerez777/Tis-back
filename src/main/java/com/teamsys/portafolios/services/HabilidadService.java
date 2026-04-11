package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.HabilidadRequestDTO;
import com.teamsys.portafolios.entities.Categoria;
import com.teamsys.portafolios.entities.Habilidad;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.CategoriaRepository;
import com.teamsys.portafolios.repositories.HabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabilidadService {

    @Autowired
    private HabilidadRepository habilidadRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Método para obtener todas las categorías disponibles
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    public List<Habilidad> listarPorUsuario(Usuario usuario) {
        return habilidadRepository.findByUsuario(usuario);
    }

    public Habilidad guardarHabilidad(HabilidadRequestDTO dto, Usuario usuario) {
        Habilidad habilidad = new Habilidad();
        habilidad.setUsuario(usuario);
        habilidad.setNombreHabilidad(dto.getNombreHabilidad());
        habilidad.setTipo(dto.getTipo());
        habilidad.setCertificado(dto.getCertificado());

        // Buscamos la categoría si viene el ID
        if (dto.getIdCategoria() != null) {
            Categoria cat = categoriaRepository.findById(dto.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            habilidad.setCategoria(cat);
        }

        // --- Lógica de validación de Tipo ---
        if (dto.getTipo() == Habilidad.TipoHabilidad.TECNICA) {
            habilidad.setNivelDominio(dto.getNivelDominio());
            habilidad.setAnosExperiencia(dto.getAnosExperiencia());
        } else {
            // Si es BLANDA, forzamos que estos campos sean nulos
            habilidad.setNivelDominio(null);
            habilidad.setAnosExperiencia(null);
        }

        return habilidadRepository.save(habilidad);
    }
}