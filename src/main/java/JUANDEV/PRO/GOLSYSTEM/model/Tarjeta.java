package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoTarjeta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table (name = "tarjeta")
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer minuto;

    @Enumerated(EnumType.STRING)
    private TipoTarjeta tipoTarjeta;

    // Una tarjeta ocurre en un partido
    @ManyToOne
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    // Jugador sancionado
    @ManyToOne
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;
}
