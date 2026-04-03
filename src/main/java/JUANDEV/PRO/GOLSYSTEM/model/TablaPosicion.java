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

    // 🔥 Puede ser tabla de grupo o general
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fase_id")
    private Fase fase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @OneToMany(mappedBy = "tabla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PosicionEquipo> posiciones = new ArrayList<>();
}