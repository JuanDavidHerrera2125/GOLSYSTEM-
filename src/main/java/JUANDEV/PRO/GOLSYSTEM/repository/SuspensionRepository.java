package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.Suspension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SuspensionRepository extends JpaRepository<Suspension, Long> {
    List<Suspension> findByJugadorIdAndActivaTrue(Long jugadorId);
}
