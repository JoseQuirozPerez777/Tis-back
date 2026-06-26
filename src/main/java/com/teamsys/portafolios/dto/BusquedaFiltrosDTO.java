package com.teamsys.portafolios.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaFiltrosDTO {
    private String buscar;             // Barra general: nombre, profesión, tecnología, empresa, etc.
    private String profesion;
    private String especializacion;
    private String tecnologia;
    private String empresa;            // Empresa donde trabajó o tiene experiencia
    private String formacionAcademica; // Nivel o área académica
    private String disponibilidad;     // Estado laboral
    private String modalidadTrabajo;   // PRESENCIAL, REMOTO, HIBRIDO
    private Integer experienciaMinima; // Años mínimos
    private List<String> idiomas;
    private String ubicacion;

    // Paginación y Ordenamiento
    private String ordenarPor;         // relevancia, experiencia, proyectos, recientes
    private int pagina = 1;            // Por defecto página 1
    private int limite = 10;           // Por defecto 10 elementos
}