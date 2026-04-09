package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import JUANDEV.PRO.GOLSYSTEM.enums.TipoFase;
import JUANDEV.PRO.GOLSYSTEM.model.Equipo;
import JUANDEV.PRO.GOLSYSTEM.model.Fase;
import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.model.ResultadoPartido;
import JUANDEV.PRO.GOLSYSTEM.model.Torneo;
import JUANDEV.PRO.GOLSYSTEM.repository.FaseRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.PartidoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.PartidoService;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PartidoServiceImpl implements PartidoService {

    private final PartidoRepository repository;
    private final FaseRepository faseRepository;
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
        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con id: " + faseId));

        if (fase.getTipoFase() != TipoFase.LIGA) {
            throw new RuntimeException("generateFixtureLiga solo aplica a fases de tipo LIGA (actual: "
                    + fase.getTipoFase() + ")");
        }

        if (fase.getTorneo() == null) {
            throw new RuntimeException("La fase no está asociada a un torneo.");
        }

        if (!fase.getPartidos().isEmpty()) {
            throw new RuntimeException("La fase ya tiene partidos. Elimínalos antes de regenerar el fixture.");
        }

        Torneo torneo = fase.getTorneo();
        List<Equipo> equipos = new ArrayList<>(torneo.getEquipos());
        if (equipos.size() < 2) {
            throw new RuntimeException("Se necesitan al menos 2 equipos inscritos en el torneo para generar la liga.");
        }

        boolean idaYVuelta = fase.getRegla() != null
                && Boolean.TRUE.equals(fase.getRegla().getTieneIdaYVuelta());

        List<Partido> primeraVuelta = generarRoundRobinBerger(fase, equipos, idaYVuelta);
        for (Partido p : primeraVuelta) {
            fase.addPartido(p);
            repository.save(p);
        }
    }

    /**
     * Round-robin circular (Berger): fija el primer slot y rota el resto cada jornada.
     * Equipos impares: se añade un hueco (null) = fecha libre por ronda.
     * Ida y vuelta: segunda vuelta con local/visitante invertidos y jornadas posteriores.
     */
    private List<Partido> generarRoundRobinBerger(Fase fase, List<Equipo> equiposTorneo, boolean idaYVuelta) {
        List<Equipo> equipos = new ArrayList<>(equiposTorneo);
        int n = equipos.size();
        if (n % 2 == 1) {
            equipos.add(null);
            n++;
        }

        int numRondas = n - 1;
        int mitad = n / 2;
        List<Equipo> rotacion = new ArrayList<>(equipos);

        List<Partido> primeraIda = new ArrayList<>();

        for (int ronda = 0; ronda < numRondas; ronda++) {
            int jornada = ronda + 1;
            for (int i = 0; i < mitad; i++) {
                Equipo a = rotacion.get(i);
                Equipo b = rotacion.get(n - 1 - i);
                if (a == null || b == null) {
                    continue;
                }
                boolean aLocal = (ronda + i) % 2 == 0;
                Equipo local = aLocal ? a : b;
                Equipo visitante = aLocal ? b : a;
                primeraIda.add(crearPartidoLiga(fase, local, visitante, jornada));
            }
            if (n > 2) {
                Equipo ultimo = rotacion.remove(n - 1);
                rotacion.add(1, ultimo);
            }
        }

        if (!idaYVuelta) {
            return primeraIda;
        }

        int maxJornada = numRondas;
        List<Partido> vuelta = new ArrayList<>();
        for (Partido p : primeraIda) {
            vuelta.add(crearPartidoLiga(
                    fase,
                    p.getEquipoVisitante(),
                    p.getEquipoLocal(),
                    maxJornada + p.getJornada()));
        }
        List<Partido> todo = new ArrayList<>(primeraIda);
        todo.addAll(vuelta);
        return todo;
    }

    private static Partido crearPartidoLiga(Fase fase, Equipo local, Equipo visitante, int jornada) {
        Partido partido = new Partido();
        partido.setFase(fase);
        partido.setGrupo(null);
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setJornada(jornada);
        partido.setEstado(EstadoPartido.PROGRAMADO);
        return partido;
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