package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tabla_posicion")
public class TablaPosicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fase_id")
    private Fase fase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @OneToMany(mappedBy = "tabla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PosicionEquipo> posiciones = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo; //

    // ================= HELPERS =================

    public void addPosicion(PosicionEquipo posicion) {
        posiciones.add(posicion);
        posicion.setTabla(this);
    }

    public void removePosicion(PosicionEquipo posicion) {
        posiciones.remove(posicion);
        posicion.setTabla(null);
    }
}