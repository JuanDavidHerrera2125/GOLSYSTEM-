package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.Escenario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EscenarioRepository extends JpaRepository<Escenario , Long> {
    Optional<Escenario> findByNombre(String nombre);
}
