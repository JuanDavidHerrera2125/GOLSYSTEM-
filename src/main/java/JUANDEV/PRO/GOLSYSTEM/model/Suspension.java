package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "suspension")
public class Suspension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partido_suspendido", nullable = false)
    private Integer partidosSuspendido;

    @Column(length = 200)
    private String motivo;

    @Column(nullable = false)
    private Boolean activa = true;

    //Jugador sancionado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

    //Partido donde ocurrió la sanción (clave en torneos reales)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_origen_id")
    private Partido partidoOrigen;
}