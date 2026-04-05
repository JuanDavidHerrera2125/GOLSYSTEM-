package JUANDEV.PRO.GOLSYSTEM.dto;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PartidoDTO {

    private Long id;

    private String equipoLocal;
    private String equipoVisitante;

    private Integer golesLocal;
    private Integer golesVisitante;

    private LocalDate fecha;
    private LocalTime hora;

    private EstadoPartido estado;

    private String escenario;
}