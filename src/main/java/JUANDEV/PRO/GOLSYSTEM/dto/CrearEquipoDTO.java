package JUANDEV.PRO.GOLSYSTEM.dto;

import lombok.Data;

@Data
public class CrearEquipoDTO {

    private String nombre;
    private String logo;
    private String ciudad;
    private String tecnico;

    private Long torneoId;
}