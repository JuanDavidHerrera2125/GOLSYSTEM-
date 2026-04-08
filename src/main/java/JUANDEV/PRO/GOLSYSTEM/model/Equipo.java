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
@Table(name = "equipo")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String logo;
    private String ciudad;
    private String tecnico;

    @Column(nullable = false)
    private Integer estrellas = 0;

    // Relación ManyToMany: Un equipo puede estar en muchos torneos
    @JsonIgnore
    @ManyToMany(mappedBy = "equipos")
    private List<Torneo> torneos = new ArrayList<>();

    // Relación OneToMany: Un equipo tiene muchos jugadores
    @JsonIgnore
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Jugador> jugadores = new ArrayList<>();

    // ================= HELPERS PARA JUGADORES =================

    /**
     * Agrega un jugador al equipo asegurando la bidireccionalidad.
     */
    public void addJugador(Jugador jugador) {
        if (jugador != null) {
            this.jugadores.add(jugador);
            jugador.setEquipo(this);
        }
    }

    /**
     * Elimina un jugador y rompe el vínculo con este equipo.
     */
    public void removeJugador(Jugador jugador) {
        if (jugador != null) {
            this.jugadores.remove(jugador);
            jugador.setEquipo(null);
        }
    }

    // ================= LÓGICA DE NEGOCIO (GOLSYSTEM) =================

    /**
     * Incrementa el palmarés del equipo cuando gana un torneo.
     */
    public void ganarEstrella() {
        this.estrellas++;
    }

    // ================= MÉTODOS DE SOPORTE =================

    /**
     * Evita problemas de recursividad en logs o debug sin usar @Data.
     */
    @Override
    public String toString() {
        return "Equipo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tecnico='" + tecnico + '\'' +
                ", estrellas=" + estrellas +
                '}';
    }
}