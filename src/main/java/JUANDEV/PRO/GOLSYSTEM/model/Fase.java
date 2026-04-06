package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoFase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "fase")
public class Fase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer orden;

    @Enumerated(EnumType.STRING)
    private TipoFase tipoFase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id")
    private Torneo torneo;

    @JsonIgnore
    @OneToMany(mappedBy = "fase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grupo> grupos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "fase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partido> partidos = new ArrayList<>();

    // ================= HELPERS =================

    public void addGrupo(Grupo grupo) {
        grupos.add(grupo);
        grupo.setFase(this);
    }

    public void removeGrupo(Grupo grupo) {
        grupos.remove(grupo);
        grupo.setFase(null);
    }

    public void addPartido(Partido partido) {
        partidos.add(partido);
        partido.setFase(this);
    }

    public void removePartido(Partido partido) {
        partidos.remove(partido);
        partido.setFase(null);
    }
}