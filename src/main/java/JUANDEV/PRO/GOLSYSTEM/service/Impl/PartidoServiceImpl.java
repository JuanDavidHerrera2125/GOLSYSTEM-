package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.model.ResultadoPartido;
import JUANDEV.PRO.GOLSYSTEM.repository.PartidoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.PartidoService;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PartidoServiceImpl implements PartidoService {

    private final PartidoRepository repository;
    private final TablaPosicionService tablaPosicionService; // Necesario para actualizar la tabla tras un W.O.

    @Override
    public Partido save(Partido partido) {
        return repository.save(partido);
    }

    @Override
    public Optional<Partido> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Partido> findAll() {
        return repository.findAll();
    }

    @Override
    public Partido update(Long id, Partido partidoActualizado) {
        return repository.findById(id).map(p -> {
            p.setFecha(partidoActualizado.getFecha());
            p.setHora(partidoActualizado.getHora());
            p.setJornada(partidoActualizado.getJornada());
            p.setEscenario(partidoActualizado.getEscenario());
            p.setEstado(partidoActualizado.getEstado());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Partido no encontrado con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void cambiarEstado(Long id, EstadoPartido nuevoEstado) {
        Partido partido = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        partido.setEstado(nuevoEstado);
        repository.save(partido);
    }

    @Override
    public void registrarWalkover(Long partidoId, Long equipoGanadorId) {
        Partido partido = repository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        // Validamos que el partido no esté ya finalizado
        if (partido.getEstado() == EstadoPartido.FINALIZADO || partido.getEstado() == EstadoPartido.WALKOVER) {
            throw new RuntimeException("El partido ya tiene un resultado definitivo.");
        }

        // Creamos el resultado de Walkover (3-0 reglamentario)
        ResultadoPartido res = new ResultadoPartido();
        res.setPartido(partido);
        res.setId(partidoId); // Sincronización @MapsId

        if (equipoGanadorId.equals(partido.getEquipoLocal().getId())) {
            res.setGolesLocal(3);
            res.setGolesVisitante(0);
        } else if (equipoGanadorId.equals(partido.getEquipoVisitante().getId())) {
            res.setGolesLocal(0);
            res.setGolesVisitante(3);
        } else {
            throw new RuntimeException("El ID del equipo ganador no pertenece a este partido.");
        }

        // Aplicamos cambios al objeto raíz (Partido)
        partido.setResultadoPartido(res);
        partido.setEstado(EstadoPartido.WALKOVER);

        // Guardamos el partido (la cascada se encarga del resultado)
        repository.save(partido);

        // ¡IMPORTANTE!: Disparamos la actualización de la tabla de posiciones
        tablaPosicionService.updateTablaFromPartido(partidoId);
    }

    @Override
    public List<Partido> obtenerPartidosPorEstado(EstadoPartido estado) {
        return repository.findByEstado(estado);
    }

    // --- MÉTODOS DE FIXTURE (Estructura lista para implementación) ---

    @Override
    public void generateFixtureLiga(Long faseId) {
        // Aquí implementarás el algoritmo de Round Robin (Todos contra todos)
        // juandev.pro: Tip - Usa el algoritmo de rotación de Berger.
    }

    @Override
    public void generateFixtureGrupos(Long faseId) {
        // Implementar lógica para repartir equipos en grupos y generar sus partidos.
    }

    @Override
    public void generateFixtureEliminatoria(Long faseId) {
        // Implementar lógica de llaves (Brackets) tipo mundial.
    }
}