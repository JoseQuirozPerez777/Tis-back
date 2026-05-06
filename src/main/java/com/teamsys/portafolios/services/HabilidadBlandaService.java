package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.HabilidadBlandaRequestDTO;
import com.teamsys.portafolios.dto.HabilidadBlandaUpdateDTO;
import com.teamsys.portafolios.entities.Categoria;
import com.teamsys.portafolios.entities.HabilidadBlanda;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.CategoriaRepository;
import com.teamsys.portafolios.repositories.HabilidadBlandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HabilidadBlandaService {

    @Autowired
    private HabilidadBlandaRepository blandaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<HabilidadBlanda> listarPorUsuario(Usuario usuario) {
        return blandaRepository.findByUsuario(usuario);
    }

    @Transactional
    public HabilidadBlanda guardar(HabilidadBlandaRequestDTO dto, Usuario usuario) {
        HabilidadBlanda habilidad = new HabilidadBlanda();
        return mapearYGuardar(habilidad, dto, usuario);
    }

    @Transactional
    public HabilidadBlanda actualizar(HabilidadBlandaUpdateDTO dto, Usuario usuario) {
        HabilidadBlanda habilidad = blandaRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Habilidad blanda no encontrada"));

        return mapearYGuardar(habilidad, dto, usuario);
    }

    public void eliminar(Long id) {
        blandaRepository.deleteById(id);
    }

    private HabilidadBlanda mapearYGuardar(HabilidadBlanda entidad, HabilidadBlandaRequestDTO dto, Usuario usuario) {
        entidad.setUsuario(usuario);
        entidad.setNombre(dto.getNombre());
        entidad.setEvidenciaUrl(dto.getEvidenciaUrl());
        entidad.setDescripcion(dto.getDescripcion());

        Categoria contexto = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Contexto (Categoría) no encontrado"));
        entidad.setCategoria(contexto);

        return blandaRepository.save(entidad);
    }
}