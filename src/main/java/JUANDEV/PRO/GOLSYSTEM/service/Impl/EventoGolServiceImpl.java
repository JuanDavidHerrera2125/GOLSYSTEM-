package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.EventoGol;
import JUANDEV.PRO.GOLSYSTEM.model.Jugador;
import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.repository.EventoGolRepository;
import JUANDEV.PRO.GOLSYSTEM.service.EventoGolService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventoGolServiceImpl implements EventoGolService {

    private final EventoGolRepository eventoGolRepository;

    public EventoGolServiceImpl(EventoGolRepository eventoGolRepository) {
        this.eventoGolRepository = eventoGolRepository;
    }

    @Override
    public EventoGol save(EventoGol eventoGol) {
        Jugador jugador = eventoGol.getJugador();
        if (jugador != null) {
            jugador.getGoles().add(eventoGol);
        }

        Partido partido = eventoGol.getPartido();
        if (partido != null) {
            partido.getGoles().add(eventoGol);
        }
        return eventoGolRepository.save(eventoGol);
    }

    @Override
    public Optional<EventoGol> findById(Long id) {
        return eventoGolRepository.findById(id);
    }

    @Override
    public List<EventoGol> findAll() {
        return eventoGolRepository.findAll();
    }

    @Override
    public EventoGol update(EventoGol eventoGol, Long id) {
        return eventoGolRepository.findById(id)
                .map(existing -> {
                    existing.setMinuto(eventoGol.getMinuto());
                    existing.setEsPenal(eventoGol.getEsPenal());
                    existing.setEsAutogol(eventoGol.getEsAutogol());

                    if (eventoGol.getJugador() != null && !eventoGol.getJugador().equals(existing.getJugador())) {
                        existing.getJugador().getGoles().remove(existing);
                        eventoGol.getJugador().getGoles().add(existing);
                        existing.setJugador(eventoGol.getJugador());
                    }

                    if (eventoGol.getPartido() != null && !eventoGol.getPartido().equals(existing.getPartido())) {
                        existing.getPartido().getGoles().remove(existing);
                        eventoGol.getPartido().getGoles().add(existing);
                        existing.setPartido(eventoGol.getPartido());
                    }

                    return eventoGolRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Gol no encontrado"));
    }

    @Override
    public void deleteById(Long id) {
        EventoGol eventoGol = eventoGolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el evento de gol con el ID: " + id));

        if (eventoGol.getJugador() != null) {
            eventoGol.getJugador().getGoles().remove(eventoGol);
        }
        if (eventoGol.getPartido() != null) {
            eventoGol.getPartido().getGoles().remove(eventoGol);
        }

        eventoGolRepository.delete(eventoGol);
    }

    @Override
    public long count() {
        return eventoGolRepository.count();
    }

    // --- AQUÍ ESTÁ EL TRIPLE EMPAREJAMIENTO EXIGIDO POR INTELLIJ ---

    @Override
    public List<EventoGol> findByEquipo_Id(Long equipoId) {
        return eventoGolRepository.findByJugador_Equipo_Id(equipoId);
    }

    @Override
    public Optional<EventoGol> findByJugador_Id(Long jugadorId) {
        return eventoGolRepository.findByJugador_Id(jugadorId);
    }

    @Override
    public Optional<EventoGol> findByPartido_Id(Long partidoId) {
        return eventoGolRepository.findByPartido_Id(partidoId);
    }
}