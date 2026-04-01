package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table (name = "equipo", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"torneo_id" , "nombre"})
})
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String logo;
    private String ciudad;
    private String tecnico;
    private Integer estrellas = 0;

    @ManyToOne
    private Torneo torneo;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Jugador>jugadores = new ArrayList<>();

    @OneToMany(mappedBy = "equipo")
    private List<TablaPosiciones>posiciones;

    @OneToMany(mappedBy = "equipoLocal")
    private List<Partido>partidosLocal;

    @OneToMany(mappedBy = "equipoVisitante")
    private List<Partido>partidosVisitante;
}
