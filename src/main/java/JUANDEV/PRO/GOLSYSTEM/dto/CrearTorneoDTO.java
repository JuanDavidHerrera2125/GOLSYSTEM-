package JUANDEV.PRO.GOLSYSTEM.dto;

import JUANDEV.PRO.GOLSYSTEM.enums.CategoriaGenero;
import lombok.Data;

@Data
public class CrearTorneoDTO {

    private String nombre;
    private String logo;
    private Integer anio;
    private String descripcion;

    private CategoriaGenero categoriaGenero;

    private Integer edadMin;
    private Integer edadMax;

    private Integer puntosVictoria;
    private Integer puntosEmpate;
    private Integer puntosDerrota;
}