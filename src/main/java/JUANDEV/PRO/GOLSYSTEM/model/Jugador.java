package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "jugador")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer numero;
    private LocalDate fechaNacimiento;
    private String documento;
    private String foto;

    @ManyToOne
    private Equipo equipo;

    // Un jugador puede anotar muchos goles
    @OneToMany(mappedBy = "jugador" , cascade = CascadeType.ALL)
    private List<Gol> goles;

    @OneToMany(mappedBy = "jugador" , cascade = CascadeType.ALL)
    private List<Tarjeta>tarjetas;

    @OneToMany(mappedBy = "jugador" , cascade = CascadeType.ALL)
    private List<Suspension>suspensiones;


}
