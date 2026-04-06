package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.EventoTarjeta;
import java.util.List;
import java.util.Optional;

public interface EventoTarjetaService {
    EventoTarjeta save(EventoTarjeta eventoTarjeta);
    Optional<EventoTarjeta> findById(Long id);
    List<EventoTarjeta> findAll();
    EventoTarjeta update(EventoTarjeta eventoTarjeta, Long id);
    void deleteById(Long id);
    long count();

    // Métodos específicos requeridos
    List<EventoTarjeta> findByEquipo_Id(Long equipoId);
    Optional<EventoTarjeta> findByJugador_Id(Long jugadorId);
    Optional<EventoTarjeta> findByPartido_Id(Long partidoId);
}