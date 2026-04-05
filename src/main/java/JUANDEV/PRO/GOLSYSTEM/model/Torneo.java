package JUANDEV.PRO.GOLSYSTEM.model;

import JUANDEV.PRO.GOLSYSTEM.enums.CategoriaGenero;
import JUANDEV.PRO.GOLSYSTEM.enums.EstadoTorneo;
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

    // ⚠️ REQUIERE @EnableJpaAuditing en configuración

    @Enumerated(EnumType.STRING)
    private EstadoTorneo estado = EstadoTorneo.CONFIGURACION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaGenero categoriaGenero;

    private Integer edadMin;
    private Integer edadMax;

    private Integer puntosVictoria = 3;
    private Integer puntosEmpate = 1;
    private Integer puntosDerrota = 0;

    // ⚠️ NO usar @ToString ni @Data (evita loops)

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipo> equipos = new ArrayList<>();

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fase> fases = new ArrayList<>();

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL)
    private List<TablaPosicion> tablas = new ArrayList<>();

    // ================= HELPERS =================

    public void addEquipo(Equipo equipo) {
        equipos.add(equipo);
        equipo.setTorneo(this);
    }

    public void removeEquipo(Equipo equipo) {
        equipos.remove(equipo);
        equipo.setTorneo(null);
    }

    public void addFase(Fase fase) {
        fases.add(fase);
        fase.setTorneo(this);
    }

    public void removeFase(Fase fase) {
        fases.remove(fase);
        fase.setTorneo(null);
    }
}