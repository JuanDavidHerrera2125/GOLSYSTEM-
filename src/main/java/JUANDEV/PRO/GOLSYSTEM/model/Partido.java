package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter @Setter
@Entity
@Table (name = "partido")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private LocalTime hora;
    private Integer jornada;

    @Enumerated(EnumType.STRING)
    private EstadoPartido estado = EstadoPartido.BORRADOR;

     //*************************************
     //************ RELACIONES *************
     //**************************************


    // Un partido pertenece a una fase (liga, grupos o eliminación)
    @ManyToOne
    @JoinColumn(name = "fase_id", nullable = false)
    private Fase fase;

    // Solo aplica si el partido es de fase de grupos (puede ser null)
    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    // Equipo local (obligatorio)
    @ManyToOne
    @JoinColumn(name = "local_id", nullable = false)
    private Equipo equipoLocal;

    // Equipo local (obligatorio)
    @ManyToOne
    @JoinColumn(name = "visitante_id", nullable = false)
    private Equipo equipoVisitante;

    @OneToOne(mappedBy = "partido", cascade = CascadeType.ALL)
    private Resultado resultado;

    // Un partido tiene muchos goles
    @OneToMany(mappedBy = "partido" , cascade = CascadeType.ALL)
    private List<Gol>goles;

    @OneToMany(mappedBy = "partido" , cascade = CascadeType.ALL)
    private List<Tarjeta>tarjetas;

}
