package JUANDEV.PRO.GOLSYSTEM.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CrearJugadorDTO {

    private String nombre;
    private Integer numero;
    private LocalDate fechaNacimiento;
    private String documento;
    private String foto;

    private Long equipoId;
}