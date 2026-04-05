package JUANDEV.PRO.GOLSYSTEM.dto;

import lombok.Data;

@Data
public class EquipoDTO {

    private Long id;
    private String nombre;
    private String logo;
    private String ciudad;
    private String tecnico;
    private Integer estrellas;
}