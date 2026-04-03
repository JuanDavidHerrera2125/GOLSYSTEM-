package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table (name = "suspension")
public class Suspension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer partidosSuspendido;
    @Column(length = 200)
    private String motivo;
    @Column(nullable = false)
    private Boolean activa = true;

    @ManyToOne
    @JoinColumn(name = "jugador_id" , nullable = false)
    private Jugador jugador;
}
