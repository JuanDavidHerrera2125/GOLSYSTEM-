package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.EventoGol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GolRepository extends JpaRepository<EventoGol, Long> {
}
