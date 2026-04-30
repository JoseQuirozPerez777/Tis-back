package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.entities.Categoria;
import com.teamsys.portafolios.services.HabilidadTecnicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habilidades")
@CrossOrigin(origins = "*") // Ajustar según tu configuración de seguridad
public class CategoriaController {

    @Autowired
    private HabilidadTecnicaService habilidadService;

    @GetMapping("/categorias")
    public ResponseEntity<?> obtenerCategorias() {
        try {
            List<Categoria> categorias = habilidadService.obtenerTodasLasCategorias();

            // Si la lista está vacía, igual devolvemos 200 con lista vacía o podrías manejar un 204
            return ResponseEntity.ok(categorias);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener categorías: " + e.getMessage());
        }
    }
}