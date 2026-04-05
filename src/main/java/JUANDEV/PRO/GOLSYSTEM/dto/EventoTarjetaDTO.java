package JUANDEV.PRO.GOLSYSTEM.dto;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoTarjeta;
import lombok.Data;

@Data
public class EventoTarjetaDTO {

    private String jugador;
    private Integer minuto;
    private TipoTarjeta tipoTarjeta;
}