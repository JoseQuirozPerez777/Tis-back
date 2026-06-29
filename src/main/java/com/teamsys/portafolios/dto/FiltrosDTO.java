package com.teamsys.portafolios.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiltrosDTO {

    // ==========================================
    // Filtros Globales
    // ==========================================

    private String buscar;
    private String profesion;
    private String especializacion;
    private String tecnologia;
    private String empresa;
    private String disponibilidad;
    private ModalidadTrabajo modalidadTrabajo;
    private Integer experienciaMinima;
    private List<String> idiomas;
    private String ubicacion;

    // ==========================================
    // Paginación
    // ==========================================

    private String ordenarPor;
    private int pagina = 1;
    private int limite = 10;

    // ==========================================
    // Bloques de filtros
    // ==========================================

    private ExperienciaLaboralFiltro experienciaLaboral;

    private HabilidadTecnicaFiltro habilidadTecnica;

    private HabilidadBlandaFiltro habilidadBlanda;

    private ProyectoFiltro proyecto;

    private FormacionAcademicaFiltro formacionAcademica;

    // ==========================================
    // Subobjetos
    // ==========================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienciaLaboralFiltro {

        private String nombreEmpresa;
        private String cargo;
        private Integer anosExperiencia;
        private String ciudad;
        private List<String> tecnologias;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HabilidadTecnicaFiltro {

        private String nombre;
        private NivelDominio nivel;
        private Integer anosExperiencia;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HabilidadBlandaFiltro {

        private String nombre;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProyectoFiltro {

        private String nombre;
        private List<String> tecnologias;
        private String duracion;
        private String rol;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormacionAcademicaFiltro {

        private String institucion;
        private String titulo;
        private NivelAcademico nivel;
        private Integer duracion;
        private EstadoFormacion estado;
    }

    // ==========================================
    // ENUMS
    // ==========================================

    public enum ModalidadTrabajo {
        REMOTO,
        PRESENCIAL,
        HIBRIDO
    }

    public enum NivelDominio {
        BASICO,
        INTERMEDIO,
        AVANZADO,
        EXPERTO
    }

    public enum NivelAcademico {
        PRIMARIA,
        SECUNDARIA,
        TECNICO,
        LICENCIATURA,
        MAESTRIA,
        DOCTORADO,
        DIPLOMADO,
        CURSOS
    }

    public enum EstadoFormacion {
        EN_CURSO,
        FINALIZADO,
        INCOMPLETO
    }
}