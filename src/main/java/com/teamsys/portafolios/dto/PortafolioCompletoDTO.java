package com.teamsys.portafolios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortafolioCompletoDTO {
    
    // Datos de Perfil del Usuario
    private String nombre;
    private String correo;
    private String foto;
    private String profesion; // Nombre de la profesión en lugar del ID
    private String biografia;
    private String telefono;
    private String direccion;
    private String enlacePublico;

    // Secciones del Portafolio
    private List<ExperienciaLaboralResumenDTO> experienciasLaborales;
    private List<FormacionAcademicaResumenDTO> formacionesAcademica;
    private List<HabilidadTecnicaResumenDTO> habilidadesTecnicas;
    private List<HabilidadBlandaResumenDTO> habilidadesBlandas;
    private List<ProyectoResumenDTO> proyectos;
    private List<RedSocialResumenDTO> redesSociales;

    // Sub-DTOs aplanados y limpios de IDs internos
    @Data @Builder
    public static class ExperienciaLaboralResumenDTO {
        private String nombreEmpresa;
        private String cargoPuesto;
        private String areaProfesional;
        private String especializacion;
        private String fechaInicio;
        private String fechaFin;
        private boolean actualmenteTrabajoAqui;
        private String modalidadTrabajo;
        private String ubicacion;
        private String tipoContrato;
        private String descripcionProyecto;
        private String evidenciaLaboralPdfUrl;
        private String proyectoRelacionadoUrl;
        private List<String> tecnologias; // Nombres de las tecnologías
    }

    @Data @Builder
    public static class FormacionAcademicaResumenDTO {
        private String institucion;
        private String tituloObtenido;
        private String nivel;
        private String area;
        private String fechaInicio;
        private String fechaFin;
        private String descripcion;
        private String estado;
        private String urlImagen;
    }

    @Data @Builder
    public static class HabilidadTecnicaResumenDTO {
        private String nombre;
        private String categoria; // Nombre de la categoría
        private String nivelDominio;
        private Integer anosExperiencia;
        private String descripcion;
        private String certificadoUrl;
    }

    @Data @Builder
    public static class HabilidadBlandaResumenDTO {
        private String nombre;
        private String categoria; // Nombre de la categoría/contexto
        private String descripcion;
        private String evidenciaUrl;
    }

    @Data @Builder
    public static class ProyectoResumenDTO {
        private String titulo;
        private String rolProyecto;
        private String descripcion;
        private List<String> urlsImagenes;
        private List<String> urlsAdicionales;
        private List<String> tecnologias; // Nombres de las tecnologías
        private String enlaceGithub;
        private String enlaceDemo;
        private String fechaInicio;
        private String fechaFinalizacion;
        private String estadoProyecto;
    }

    @Data @Builder
    public static class RedSocialResumenDTO {
        private String nombreRed;
        private String urlPerfil;
    }
}
