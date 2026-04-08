package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoDecision;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "resultado_partido")
public class ResultadoPartido {

    @Id
    private Long id; // Este ID será el mismo partido_id gracias a @MapsId

    private Integer golesLocal = 0;
    private Integer golesVisitante = 0;

    private Integer golesLocalExtra = 0;
    private Integer golesVisitanteExtra = 0;

    private Integer penalesLocal = 0;
    private Integer penalesVisitante = 0;

    @Enumerated(EnumType.STRING)
    private TipoDecision tipoDecision;

    @OneToOne
    @MapsId
    @JoinColumn(name = "partido_id")
    @JsonIgnore // Evitar recursividad al serializar el partido
    private Partido partido;
}