package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.EventoTarjeta;
import JUANDEV.PRO.GOLSYSTEM.repository.EventoTarjetaRepository;
import JUANDEV.PRO.GOLSYSTEM.service.EventoTarjetaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventoTarjetaServiceImpl implements EventoTarjetaService {

    //Cambiado a final para inyección limpia por constructor
    private final EventoTarjetaRepository eventoTarjetaRepository;

    //Constructor para inyectar la dependencia (adiós advertencia "never assigned")
    public EventoTarjetaServiceImpl(EventoTarjetaRepository eventoTarjetaRepository) {
        this.eventoTarjetaRepository = eventoTarjetaRepository;
    }

    @Override
    public List<EventoTarjeta> findAll() {
        return eventoTarjetaRepository.findAll();
    }

    @Override
    public Optional<EventoTarjeta> findById(Long id) {
        return eventoTarjetaRepository.findById(id);
    }

    @Override
    public EventoTarjeta save(EventoTarjeta eventoTarjeta) {
        return eventoTarjetaRepository.save(eventoTarjeta);
    }

    @Override
    public EventoTarjeta update(EventoTarjeta eventoTarjeta, Long id) {
        return eventoTarjetaRepository.findById(id)
                .map(existing -> {
                    // Corregido typo de ortografía que marcaba IntelliJ
                    existing.setMinuto(eventoTarjeta.getMinuto());
                    existing.setTipoTarjeta(eventoTarjeta.getTipoTarjeta());

                    // RELACIÓN: Si cambia el jugador, sincronizamos
                    if (eventoTarjeta.getJugador() != null && !eventoTarjeta.getJugador().equals(existing.getJugador())) {
                        existing.getJugador().getTarjetas().remove(existing);
                        eventoTarjeta.getJugador().getTarjetas().add(existing);
                        existing.setJugador(eventoTarjeta.getJugador());
                    }

                    // RELACIÓN: Si cambia el partido, sincronizamos
                    if (eventoTarjeta.getPartido() != null && !eventoTarjeta.getPartido().equals(existing.getPartido())) {
                        existing.getPartido().getTarjetas().remove(existing);
                        eventoTarjeta.getPartido().getTarjetas().add(existing);
                        existing.setPartido(eventoTarjeta.getPartido());
                    }
                    return eventoTarjetaRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("No se encontró tarjeta con el ID"));
    }

    @Override
    public void deleteById(Long id) {
        EventoTarjeta eventoTarjeta = eventoTarjetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró tarjeta con el ID: " + id));

        // Remover de colecciones (consistencia bidireccional)
        if (eventoTarjeta.getJugador() != null) {
            eventoTarjeta.getJugador().getTarjetas().remove(eventoTarjeta);
        }
        if (eventoTarjeta.getPartido() != null) {
            eventoTarjeta.getPartido().getTarjetas().remove(eventoTarjeta);
        }
        eventoTarjetaRepository.delete(eventoTarjeta);
    }

    @Override
    public long count() {
        return eventoTarjetaRepository.count();
    }

    @Override
    public List<EventoTarjeta> findByEquipo_Id(Long equipoId) {
        // Llama al nuevo método corregido del repositorio
        return eventoTarjetaRepository.findByJugador_Equipo_Id(equipoId);
    }

    @Override
    public Optional<EventoTarjeta> findByJugador_Id(Long jugadorId) {
        return eventoTarjetaRepository.findByJugador_Id(jugadorId);
    }

    @Override
    public Optional<EventoTarjeta> findByPartido_Id(Long partidoId) {
        return eventoTarjetaRepository.findByPartido_Id(partidoId);
    }
}