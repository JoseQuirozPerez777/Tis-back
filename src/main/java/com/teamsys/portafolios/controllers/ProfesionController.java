package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.entities.Profesion;
import com.teamsys.portafolios.repositories.ProfesionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profesiones")
public class ProfesionController {

    @Autowired
    private ProfesionRepository profesionRepository;

    @GetMapping
    public List<Profesion> listarTodas() {
        return profesionRepository.findAll();
    }
}