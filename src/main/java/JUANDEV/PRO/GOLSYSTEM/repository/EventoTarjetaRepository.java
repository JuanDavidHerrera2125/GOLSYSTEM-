package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.EventoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EventoTarjetaRepository extends JpaRepository<EventoTarjeta, Long> {

    // 🔥 CORREGIDO: Apuntando explícitamente al ID de la relación
    Optional<EventoTarjeta> findByPartidoId(Long partidoId);

    Optional<EventoTarjeta> findByJugadorId(Long jugadorId);
}