package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.HabilidadTecnicaRequestDTO;
import com.teamsys.portafolios.dto.HabilidadTecnicaUpdateDTO;
import com.teamsys.portafolios.entities.Categoria;
import com.teamsys.portafolios.entities.HabilidadTecnica;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.CategoriaRepository;
import com.teamsys.portafolios.repositories.HabilidadTecnicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HabilidadTecnicaService {

    @Autowired
    private HabilidadTecnicaRepository tecnicaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Método para obtener todas las categorías disponibles
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    public List<HabilidadTecnica> listarPorUsuario(Usuario usuario) {
        return tecnicaRepository.findByUsuario(usuario);
    }

    @Transactional
    public HabilidadTecnica guardar(HabilidadTecnicaRequestDTO dto, Usuario usuario) {
        HabilidadTecnica habilidad = new HabilidadTecnica();
        return mapearYGuardar(habilidad, dto, usuario);
    }

    @Transactional
    public HabilidadTecnica actualizar(HabilidadTecnicaUpdateDTO dto, Usuario usuario) {
        HabilidadTecnica habilidad = tecnicaRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Habilidad técnica no encontrada"));

        return mapearYGuardar(habilidad, dto, usuario);
    }

    public void eliminar(Long id) {
        tecnicaRepository.deleteById(id);
    }

    private HabilidadTecnica mapearYGuardar(HabilidadTecnica entidad, HabilidadTecnicaRequestDTO dto, Usuario usuario) {
        entidad.setUsuario(usuario);
        entidad.setNombre(dto.getNombre());
        entidad.setNivelDominio(dto.getNivelDominio());
        entidad.setAnosExperiencia(dto.getAnosExperiencia());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setCertificadoUrl(dto.getCertificadoUrl());

        Categoria cat = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría técnica no encontrada"));
        entidad.setCategoria(cat);

        return tecnicaRepository.save(entidad);
    }
}