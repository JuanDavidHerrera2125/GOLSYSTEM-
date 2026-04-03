package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "evento_gol")
public class EventoGol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer minuto;

    @Column(name = "es_penal")
    private Boolean esPenal;

    @Column(name = "es_autogol")
    private Boolean esAutogol;

    // ================= RELACIONES =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;
}