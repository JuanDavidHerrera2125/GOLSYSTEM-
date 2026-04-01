package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.CategoriaGenero;
import JUANDEV.PRO.GOLSYSTEM.enums.EstadoTorneo;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.persistence.autoconfigure.EntityScan;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "torneo")
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String logo;

    @Column(nullable = false)
    private Integer anio;

    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    private EstadoTorneo estado = EstadoTorneo.CONFIGURACION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaGenero categoriaGenero;

    // ================= CONFIGURACION DE EDADES =================
    private Integer edadMin;
    private Integer edadMax;

    // ================= CONFIGURACION DE PUNTOS =================
    private Integer puntosVictoria = 3;
    private Integer puntosEmpate = 1;
    private Integer puntosDerrota = 0;

    // ================= RELACIONES =================

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL , orphanRemoval = true , fetch = FetchType.LAZY)
    private List<Equipo>equipos = new ArrayList<>();

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Fase>fases = new ArrayList<>();

    @OneToMany(mappedBy = "torneo" , fetch = FetchType.LAZY)
    private List<TablaPosiciones>tablaPosiciones;

    @OneToMany(mappedBy = "torneo", fetch = FetchType.LAZY)
    private List<Premio>premios;

    @OneToMany(mappedBy = "torneo" , fetch = FetchType.LAZY)
    private List<HistorialCampeon>historial;


}
