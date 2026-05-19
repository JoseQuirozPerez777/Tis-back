package com.teamsys.portafolios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "experiencias_laborales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperienciaLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "nombre_empresa", nullable = false)
    private String nombreEmpresa;

    @Column(name = "cargo_puesto", nullable = false)
    private String cargoPuesto;

    @Column(name = "area_profesional")
    private String areaProfesional;

    private String especializacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "actualmente_trabajo_aqui")
    private boolean actualmenteTrabajoAqui;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad_trabajo")
    private ModalidadTrabajo modalidadTrabajo;

    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contrato")
    private TipoContrato tipoContrato;

    /**
     * Se almacena como una colección de elementos básicos.
     * Crea una tabla secundaria automática llamada 'experiencia_tecnologias'
     */
    @ManyToMany
    @JoinTable(
            name = "experiencia_tecnologias",
            joinColumns = @JoinColumn(name = "experiencia_id"),
            inverseJoinColumns = @JoinColumn(name = "tecnologia_id")
    )
    private List<Tecnologia> tecnologiasHerramientas;

    @Column(name = "descripcion_proyecto", columnDefinition = "TEXT")
    private String descripcionProyecto;

    @Column(name = "evidencia_laboral_pdf_url")
    private String evidenciaLaboralPdfUrl;

    @Column(name = "proyecto_relacionado_url")
    private String proyectoRelacionadoUrl;

    private boolean esPublico=true;
    // --- ENUMS INTERNOS ---

    public enum ModalidadTrabajo {
        PRESENCIAL, REMOTO, HIBRIDO
    }

    public enum TipoContrato {
        TIEMPO_COMPLETO, MEDIO_TIEMPO, PASANTIA, PROYECTO, FREELANCE
    }
}