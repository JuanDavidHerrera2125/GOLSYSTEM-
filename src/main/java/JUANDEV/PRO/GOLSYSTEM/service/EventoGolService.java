package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.EventoGol;

import java.util.List;
import java.util.Optional;

public interface EventoGolService {

    //Guardar un nuevo gol
    EventoGol save (EventoGol eventoGol);

    //Buscar gol por Id
    Optional<EventoGol> findById (Long id);

    //Obtener todos los goles
    List<EventoGol> findAll();

    //Actualizar evento gol
    EventoGol update (EventoGol eventoGol , Long id);

    //Eliminar gol
    void deleteById (Long id);

    //Listar goles por jugador
    List<EventoGol> findByJugadorId(Long jugadorId);

    //Listar Goles por equipo
    List<EventoGol> findByEquipoId(Long equipoId);

    //Listar goles por partido
    List<EventoGol> findByPartidoId(Long partidoId);

    //Contar todos los goles
    long count();
}
