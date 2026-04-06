package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.EventoGol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventoGolRepository extends JpaRepository<EventoGol, Long> {

    // Busca goles por el ID del jugador
    Optional<EventoGol> findByJugador_Id(Long jugadorId);

    // Busca goles por el ID del partido
    Optional<EventoGol> findByPartido_Id(Long partidoId);

    // Busca los goles a través de la relación del jugador con su equipo
    List<EventoGol> findByJugador_Equipo_Id(Long equipoId);

    long countByPartidoIdAndJugadorId(Long partidoId, Long jugadorId);
}