package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.CategoriaGenero;
import JUANDEV.PRO.GOLSYSTEM.enums.EstadoTorneo;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@EntityScan
@Table(name = "torneo")
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String logo;

    private Integer anio;

    private String descripcion;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private EstadoTorneo estado = EstadoTorneo.CONFIGURACION;

    @Enumerated(EnumType.STRING)
    private CategoriaGenero categoriaGenero;

    // ================= CONFIGURACION DE EDADES =================
    private Integer edadMin;
    private Integer edadMax;

    // ================= CONFIGURACION DE PUNTOS =================
    private Integer puntosVictoria = 3;
    private Integer puntosEmpate = 1;
    private Integer puntosDerrota = 0;

    // ================= RELACIONES =================

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Equipo>equipos = new ArrayList<>();

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fase>fases = new ArrayList<>();


}
