package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.CategoriaGenero;
import JUANDEV.PRO.GOLSYSTEM.enums.EstadoTorneo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "torneo")
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String logo;

    @Column(nullable = false)
    private Integer anio;

    private String descripcion;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private EstadoTorneo estado = EstadoTorneo.CONFIGURACION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaGenero categoriaGenero;

    private Integer edadMin;
    private Integer edadMax;

    // Configuración de puntuación estándar
    private Integer puntosVictoria = 3;
    private Integer puntosEmpate = 1;
    private Integer puntosDerrota = 0;

    // --- NUEVOS CAMPOS PARA FLEXIBILIDAD ---
    private Boolean esIdaYVueltaDefault = false;
    private Integer cantidadEquiposClasificanDefault = 2;
    // ---------------------------------------

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "torneo_equipo",
            joinColumns = @JoinColumn(name = "torneo_id"),
            inverseJoinColumns = @JoinColumn(name = "equipo_id")
    )
    private List<Equipo> equipos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fase> fases = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL)
    private List<TablaPosicion> tablas = new ArrayList<>();

    public void addEquipo(Equipo equipo) {
        if (!this.equipos.contains(equipo)) {
            this.equipos.add(equipo);
            equipo.getTorneos().add(this);
        }
    }

    public void removeEquipo(Equipo equipo) {
        this.equipos.remove(equipo);
        equipo.getTorneos().remove(this);
    }

    public void addFase(Fase fase) {
        this.fases.add(fase);
        fase.setTorneo(this);
    }

    public void addTabla(TablaPosicion tabla) {
        this.tablas.add(tabla);
        tabla.setTorneo(this);
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = java.time.LocalDateTime.now();
        }
    }
}