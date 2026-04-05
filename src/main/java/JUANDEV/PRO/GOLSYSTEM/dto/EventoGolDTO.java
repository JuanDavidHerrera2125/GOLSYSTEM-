package JUANDEV.PRO.GOLSYSTEM.dto;

import lombok.Data;

@Data
public class EventoGolDTO {

    private String jugador;
    private Integer minuto;
    private Boolean esPenal;
    private Boolean esAutogol;
}