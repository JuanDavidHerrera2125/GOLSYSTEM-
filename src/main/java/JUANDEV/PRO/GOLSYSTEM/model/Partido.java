package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "partido")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private LocalTime hora;

    // ⚠️ Temporal (luego será entidad Jornada)
    private Integer jornada;

    @Enumerated(EnumType.STRING)
    private EstadoPartido estado = EstadoPartido.BORRADOR;

    // ================= RELACIONES =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fase_id", nullable = false)
    private Fase fase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    private Equipo equipoLocal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitante_id", nullable = false)
    private Equipo equipoVisitante;

    @OneToOne(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private ResultadoPartido resultadoPartido;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoGol> goles = new ArrayList<>();

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoTarjeta> tarjetas = new ArrayList<>();
}