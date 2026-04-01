package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table (name = "TablePosiciones")
public class TablaPosiciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //**************** RELACIONES *********************

    @ManyToOne
    private Equipo equipo;
}
