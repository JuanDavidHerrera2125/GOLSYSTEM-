package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.EventoGol;
import java.util.List;
import java.util.Optional;

public interface EventoGolService {
    EventoGol save(EventoGol eventoGol);
    Optional<EventoGol> findById(Long id);
    List<EventoGol> findAll();
    EventoGol update(EventoGol eventoGol, Long id);
    void deleteById(Long id);
    long count();

    // Estos son los métodos específicos que te exige IntelliJ
    List<EventoGol> findByEquipo_Id(Long equipoId);
    Optional<EventoGol> findByJugador_Id(Long jugadorId);
    Optional<EventoGol> findByPartido_Id(Long partidoId);
}