package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoDecision;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "resultado_partido")
public class ResultadoPartido {

    @Id
    private Long id;

    private Integer golesLocal;
    private Integer golesVisitante;

    private Integer golesLocalExtra;
    private Integer golesVisitanteExtra;

    private Integer penalesLocal;
    private Integer penalesVisitante;

    @Enumerated(EnumType.STRING)
    private TipoDecision tipoDecision;

    // 🔥 Relación PRO 1:1 compartiendo PK
    @OneToOne
    @MapsId
    @JoinColumn(name = "partido_id")
    private Partido partido;
}