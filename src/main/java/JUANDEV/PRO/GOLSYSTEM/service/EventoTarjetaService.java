package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.EventoTarjeta;

import java.util.List;
import java.util.Optional;

public interface EventoTarjetaService {

    //Obtener todas las tarjetas
    List<EventoTarjeta> findAll();

    //Obtener Tarjeta por Id
    Optional<EventoTarjeta> findById(Long id);

    //Guardar tarjeta
    EventoTarjeta save (EventoTarjeta eventoTarjeta);

    //Actualizar una Tarjeta
    EventoTarjeta update (EventoTarjeta eventoTarjeta, Long id);

    //Eliminar tarjeta por Id
    void deleteById(Long id);

    //contar registros
    long count();

    //Buscar Tarjeta por partido
    Optional<EventoTarjeta> findByPartido (Long partidoId);

    // Buscar tarjetas por jugador
    Optional<EventoTarjeta> findByJugador (Long jugadorId);

}
