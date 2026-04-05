package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "posicion_equipo",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tabla_id", "equipo_id"})
        }
)
public class PosicionEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer pj = 0;
    private Integer pg = 0;
    private Integer pe = 0;
    private Integer pp = 0;

    private Integer gf = 0;
    private Integer gc = 0;
    private Integer dg = 0;

    private Integer puntos = 0;

    private Integer amarillas = 0;
    private Integer rojas = 0;

    private Integer posicion;

    // BLOQUEO OPTIMISTA PARA PRODUCCIÓN
    @Version
    private Long version;

    // ================= RELACIONES =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabla_id", nullable = false)
    private TablaPosicion tabla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;
}