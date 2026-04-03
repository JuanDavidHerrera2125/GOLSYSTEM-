package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table (name = "TablePosiciones",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"torneo_id" , "equipo_id"})
        }
)
public class TablaPosiciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer pj;
    private Integer pg;
    private Integer pe;
    private Integer pp;
    private Integer gf;
    private Integer gc;
    private Integer dg;
    private Integer puntos;
    private Integer amarillas;
    private Integer rojas;

    //**************** RELACIONES *********************

    @ManyToOne
    @JoinColumn(name = "torneo_id" , nullable = false)
    private Torneo torneo;

    @ManyToOne
    @JoinColumn(name = "equipo_id" , nullable = false)
    private Equipo equipo;
}
