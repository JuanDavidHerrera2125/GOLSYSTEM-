package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.EventoGol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventoGolRepository extends JpaRepository<EventoGol, Long> {

    List<EventoGol> findByJugadorId(Long jugadorId);

    // Apuntando explícitamente al ID de la relación
    List<EventoGol> findByPartidoId(Long partidoId);
}