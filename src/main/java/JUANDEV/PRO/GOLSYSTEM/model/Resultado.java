package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoDecision;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@Entity
@Table (name = "resultado")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //****************** RESULTADO PARTIDO ********************

    private Integer golesLocal = 0;
    private Integer golesVisitante = 0;

    private Integer golesLocalExtra = 0;
    private Integer golesVisitanteExtra = 0;

    private Integer penalesLocal;
    private Integer penalesVisitante;

    @Enumerated(EnumType.STRING)
    private TipoDecision tipoDecision;

    //********************** RELACIÓN ***************************

    // Cada partido tiene un único resultado
    @OneToOne
    @JoinColumn(name = "partido_id" , unique = true)
    private Partido partido;
}
