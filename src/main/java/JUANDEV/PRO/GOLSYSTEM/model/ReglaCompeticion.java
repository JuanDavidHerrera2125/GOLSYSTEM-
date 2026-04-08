package JUANDEV.PRO.GOLSYSTEM.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "regla_competicion")
public class ReglaCompeticion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean tieneIdaYVuelta = false;
    private Boolean permiteEmpate = true;

    // Si es true, activa lógica de penales o tiempo extra en el Service de Partidos
    private Boolean esMuerteSubita = false;

    private Integer clasificadosPorGrupo = 2;
    private Integer cantidadMejoresTerceros = 0;

    // Puntos personalizados por fase
    private Integer puntosVictoriaOverride;
    private Integer puntosEmpateOverride;

    @OneToOne(mappedBy = "regla")
    @JsonIgnore
    private Fase fase;
}