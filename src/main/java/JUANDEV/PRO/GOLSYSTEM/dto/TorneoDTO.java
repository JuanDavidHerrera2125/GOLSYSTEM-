package JUANDEV.PRO.GOLSYSTEM.dto;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoTorneo;
import JUANDEV.PRO.GOLSYSTEM.enums.CategoriaGenero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TorneoDTO {

    private Long id;
    private String nombre;
    private String logo;
    private Integer anio;
    private String descripcion;

    private EstadoTorneo estado;
    private CategoriaGenero categoriaGenero;

    private Integer edadMin;
    private Integer edadMax;

    private Integer puntosVictoria;
    private Integer puntosEmpate;
    private Integer puntosDerrota;

    private LocalDateTime createdAt;
}