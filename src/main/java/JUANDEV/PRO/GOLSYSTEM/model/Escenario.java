package JUANDEV.PRO.GOLSYSTEM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table (name = "escenario")
public class Escenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;


    //Relación con los partidos que se juegan en este escenario
    //Evitar usar Lombok @Data o toString que incluya esta lista para prevenir loops infinitos
    @OneToMany(mappedBy = "escenario" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Partido> partidos = new ArrayList<>();
}
