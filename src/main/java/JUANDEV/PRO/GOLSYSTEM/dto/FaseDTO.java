package JUANDEV.PRO.GOLSYSTEM.dto;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoFase;
import lombok.Data;

@Data
public class FaseDTO {

    private Long id;
    private String nombre;
    private Integer orden;
    private TipoFase tipoFase;
}
