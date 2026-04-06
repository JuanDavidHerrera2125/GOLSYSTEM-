package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.EventoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventoTarjetaRepository extends JpaRepository<EventoTarjeta, Long> {

    // Busca tarjetas por el ID del jugador
    Optional<EventoTarjeta> findByJugador_Id(Long jugadorId);

    // Busca tarjetas por el ID del partido
    Optional<EventoTarjeta> findByPartido_Id(Long partidoId);

    // CORRECCIÓN AQUÍ: Buscamos las tarjetas a través de la relación del jugador con su equipo
    List<EventoTarjeta> findByJugador_Equipo_Id(Long equipoId);
}