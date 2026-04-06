package JUANDEV.PRO.GOLSYSTEM.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TablaPosicionDTO {

    private Long equipoId;
    private String nombreEquipo;
    private Integer partidosJugados = 0;
    private Integer partidosGanados = 0;
    private Integer partidosEmpatados = 0;
    private Integer partidosPerdidos = 0;
    private Integer golesAFavor = 0;
    private Integer golesEnContra = 0;
    private Integer diferenciaGoles = 0;
    private Integer puntos = 0;

    // Constructor rápido para inicializar la fila de un equipo
    public TablaPosicionDTO(Long equipoId, String nombreEquipo) {
        this.equipoId = equipoId;
        this.nombreEquipo = nombreEquipo;
    }
}