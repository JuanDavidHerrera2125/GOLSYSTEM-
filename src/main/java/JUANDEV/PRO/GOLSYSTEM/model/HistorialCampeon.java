package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table (name = "historial_campeon")
public class HistorialCampeon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer anio;

    // El campeonato pertenece a un torneo específico (ej: Liga 2025)
    @ManyToOne
    @JoinColumn(name = "torneo_id" , nullable = false)
    private Torneo torneo;

    // Equipo que ganó ese torneo
    @ManyToOne
    @JoinColumn(name = "equipo_id" , nullable = false)
    private Equipo equipo;

}
