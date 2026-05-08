package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.TecnologiaResponseDTO;
import com.teamsys.portafolios.services.TecnologiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tecnologias")
@CrossOrigin(origins = "*") // Permite peticiones desde tu Frontend (Angular/React/etc.)
public class TecnologiaController {

    @Autowired
    private TecnologiaService tecnologiaService;

    /**
     * Obtiene todas las tecnologías disponibles en el catálogo global.
     * Útil para llenar selects en formularios y filtros en el buscador.
     */
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        List<TecnologiaResponseDTO> tecnologias = tecnologiaService.listarTodas();
        return ResponseEntity.ok(Map.of("data", tecnologias));
    }

    /**
     * Opcional: Filtrar tecnologías por categoría (Frontend, Backend, etc.)
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<?> listarPorCategoria(@PathVariable String categoria) {
        List<TecnologiaResponseDTO> tecnologias = tecnologiaService.listarPorCategoria(categoria);
        return ResponseEntity.ok(Map.of("data", tecnologias));
    }
}