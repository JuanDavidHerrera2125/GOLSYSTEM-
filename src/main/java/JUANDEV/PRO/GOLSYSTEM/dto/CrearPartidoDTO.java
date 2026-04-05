package JUANDEV.PRO.GOLSYSTEM.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CrearPartidoDTO {

    private Long faseId;
    private Long grupoId;

    private Long equipoLocalId;
    private Long equipoVisitanteId;

    private Long escenarioId;

    private LocalDate fecha;
    private LocalTime hora;
}