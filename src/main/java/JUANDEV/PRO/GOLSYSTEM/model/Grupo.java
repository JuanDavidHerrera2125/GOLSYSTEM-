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
@Table(name = "grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fase_id")
    private Fase fase;

    @JsonIgnore
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrupoEquipo> equipos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    private List<Partido> partidos = new ArrayList<>();

    // ================= HELPERS =================

    public void addEquipo(GrupoEquipo ge) {
        equipos.add(ge);
        ge.setGrupo(this);
    }

    public void removeEquipo(GrupoEquipo ge) {
        equipos.remove(ge);
        ge.setGrupo(null);
    }
}