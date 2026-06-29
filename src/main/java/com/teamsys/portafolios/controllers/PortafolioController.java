package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.*;
import com.teamsys.portafolios.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/portafolios")
public class PortafolioController {

    @Autowired
    private PortafolioService portafolioService;

    @Autowired
    private BusquedaService busquedaService;

    @PostMapping("/buscar")
    public ResponseEntity<?> buscarPortafolios(
            @RequestBody BusquedaFiltrosDTO filtros,
            Authentication authentication
    ) {
        try {
            String correoUsuarioActual = authentication != null
                    ? authentication.getName()
                    : null;

            ResultadoBusquedaDTO resultado = portafolioService.buscarPortafoliosConFiltros(
                    filtros,
                    correoUsuarioActual
            );

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error al procesar la búsqueda avanzada: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/avanzada/buscar")
    public ResponseEntity<?> buscarPortafoliosNuevo(
            @RequestBody FiltrosDTO filtros,
            Authentication authentication
    ) {
        try {
            String correoUsuarioActual = authentication != null
                    ? authentication.getName()
                    : null;

            ResultadoBusquedaDTO resultado = busquedaService.buscarPortafoliosConFiltros(
                    filtros,
                    correoUsuarioActual
            );

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error al procesar la búsqueda avanzada: " + e.getMessage()
            ));
        }
    }
}