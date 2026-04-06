package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private Integer jornada;

    @Enumerated(EnumType.STRING)
    private EstadoPartido estado = EstadoPartido.BORRADOR;

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

    // 🔥 NUEVA RELACIÓN CORRECTA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escenario_id")
    private Escenario escenario;

    @JsonIgnore
    @OneToOne(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private ResultadoPartido resultadoPartido;

    @JsonIgnore
    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoGol> goles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoTarjeta> tarjetas = new ArrayList<>();

    // ================= MÉTODOS DE CONVENIENCIA (Sincronización) =================

    /**
     * Agrega un gol a la lista del partido y establece la relación inversa.
     */
    public void addGol(EventoGol gol) {
        this.goles.add(gol);
        gol.setPartido(this); // Sincronización bidireccional
    }

    /**
     * Agrega una tarjeta a la lista del partido y establece la relación inversa.
     */
    public void addTarjeta(EventoTarjeta tarjeta) {
        this.tarjetas.add(tarjeta);
        tarjeta.setPartido(this); // Sincronización bidireccional
    }

}