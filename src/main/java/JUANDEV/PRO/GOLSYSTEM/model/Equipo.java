package JUANDEV.PRO.GOLSYSTEM.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "equipo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"torneo_id", "nombre"}))
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String logo;
    private String ciudad;
    private String tecnico;
    private Integer estrellas = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id")
    private Torneo torneo;

    //NO usar @ToString ni @Data

    @JsonIgnore
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Jugador> jugadores = new ArrayList<>();

    // ================= HELPERS =================

    public void addJugador(Jugador jugador) {
        jugadores.add(jugador);
        jugador.setEquipo(this);
    }

    public void removeJugador(Jugador jugador) {
        jugadores.remove(jugador);
        jugador.setEquipo(null);
    }
}