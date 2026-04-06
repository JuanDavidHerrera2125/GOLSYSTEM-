package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.dto.TablaPosicionDTO;
import JUANDEV.PRO.GOLSYSTEM.enums.EstadoTorneo;
import JUANDEV.PRO.GOLSYSTEM.enums.TipoFase;
import JUANDEV.PRO.GOLSYSTEM.model.Equipo;
import JUANDEV.PRO.GOLSYSTEM.model.Fase;
import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.model.Torneo;
import JUANDEV.PRO.GOLSYSTEM.repository.TorneoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.FaseService;
import JUANDEV.PRO.GOLSYSTEM.service.PartidoService;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import JUANDEV.PRO.GOLSYSTEM.service.TorneoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TorneoServiceImpl implements TorneoService {

    private final TorneoRepository torneoRepository;
    private final FaseService faseService;
    private final PartidoService partidoService;
    private final TablaPosicionService tablaService;

    public TorneoServiceImpl(TorneoRepository torneoRepository,
                             FaseService faseService,
                             PartidoService partidoService,
                             TablaPosicionService tablaService) {
        this.torneoRepository = torneoRepository;
        this.faseService = faseService;
        this.partidoService = partidoService;
        this.tablaService = tablaService;
    }

    // ================= CRUD =================

    @Override
    public Torneo save(Torneo torneo) {
        return torneoRepository.save(torneo);
    }

    @Override
    public Optional<Torneo> findById(Long id) {
        return torneoRepository.findById(id);
    }

    @Override
    public List<Torneo> findAll() {
        return torneoRepository.findAll();
    }

    @Override
    public Torneo update(Long id, Torneo torneo) {
        return torneoRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(torneo.getNombre());
                    existing.setDescripcion(torneo.getDescripcion());
                    existing.setAnio(torneo.getAnio());
                    existing.setCategoriaGenero(torneo.getCategoriaGenero());
                    existing.setEdadMin(torneo.getEdadMin());
                    existing.setEdadMax(torneo.getEdadMax());
                    return torneoRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con id: " + id));

        torneoRepository.delete(torneo);
    }

    @Override
    public long count() {
        return torneoRepository.count();
    }

    // ================= FLUJO DEL TORNEO =================

    @Override
    public Torneo createTorneo(Torneo torneo) {
        torneo.setEstado(EstadoTorneo.CONFIGURACION);
        return torneoRepository.save(torneo);
    }

    @Override
    public void generateTorneo(Long torneoId) {
        Torneo torneo = getTorneoOrThrow(torneoId);

        if (torneo.getEstado() != EstadoTorneo.CONFIGURACION) {
            throw new RuntimeException("El torneo ya fue generado o iniciado");
        }

        if (torneo.getFases().isEmpty()) {
            throw new RuntimeException("El torneo no tiene fases configuradas");
        }

        for (Fase fase : torneo.getFases()) {
            if (fase.getTipoFase() == TipoFase.LIGA) {
                partidoService.generateFixtureLiga(fase.getId());
            } else if (fase.getTipoFase() == TipoFase.GRUPOS) {
                partidoService.generateFixtureGrupos(fase.getId());
            } else if (fase.getTipoFase() == TipoFase.ELIMINACION) {
                partidoService.generateFixtureEliminatoria(fase.getId());
            }
        }

        torneo.setEstado(EstadoTorneo.FIXTURE_GENERADO);
        torneoRepository.save(torneo);
    }

    @Override
    public void startTorneo(Long torneoId) {
        Torneo torneo = getTorneoOrThrow(torneoId);

        if (torneo.getEstado() != EstadoTorneo.FIXTURE_GENERADO) {
            throw new RuntimeException("El torneo no está listo para iniciar");
        }

        torneo.setEstado(EstadoTorneo.EN_CURSO);
        torneoRepository.save(torneo);
    }

    @Override
    public void finishTorneo(Long torneoId) {
        Torneo torneo = getTorneoOrThrow(torneoId);

        if (torneo.getEstado() != EstadoTorneo.EN_CURSO) {
            throw new RuntimeException("El torneo no está en curso");
        }

        torneo.setEstado(EstadoTorneo.FINALIZADO);
        torneoRepository.save(torneo);
    }

    @Override
    public void archiveTorneo(Long torneoId) {
        Torneo torneo = getTorneoOrThrow(torneoId);

        if (torneo.getEstado() != EstadoTorneo.FINALIZADO) {
            throw new RuntimeException("Solo se pueden archivar torneos finalizados");
        }

        torneo.setEstado(EstadoTorneo.ARCHIVADO);
        torneoRepository.save(torneo);
    }

    // ================= CONSULTA DE TABLA DINÁMICA =================

    @Override
    public List<TablaPosicionDTO> calcularTablaAcumulada(Long torneoId) {
        return procesarCalculoDeTabla(torneoId, null);
    }

    @Override
    public List<TablaPosicionDTO> calcularTablaHastaJornada(Long torneoId, Integer jornada) {
        return procesarCalculoDeTabla(torneoId, jornada);
    }

    private List<TablaPosicionDTO> procesarCalculoDeTabla(Long torneoId, Integer jornadaLimite) {
        Torneo torneo = getTorneoOrThrow(torneoId);

        Map<Long, TablaPosicionDTO> mapaTabla = new HashMap<>();
        for (Equipo equipo : torneo.getEquipos()) {
            mapaTabla.put(equipo.getId(), new TablaPosicionDTO(equipo.getId(), equipo.getNombre()));
        }

        for (Fase fase : torneo.getFases()) {
            for (Partido partido : fase.getPartidos()) {

                if (jornadaLimite != null && partido.getJornada() > jornadaLimite) {
                    continue;
                }

                // Usamos el enum EstadoPartido que vi en tu código (FINALIZADO)
                if ("FINALIZADO".equals(partido.getEstado().toString())) {

                    int golesLocal = 0;
                    int golesVisitante = 0;

                    // ⚽ SACAMOS LOS GOLES DE TU OBJETO RESULTADO_PARTIDO SI EXISTE
                    if (partido.getResultadoPartido() != null) {
                        golesLocal = partido.getResultadoPartido().getGolesLocal() != null ? partido.getResultadoPartido().getGolesLocal() : 0;
                        golesVisitante = partido.getResultadoPartido().getGolesVisitante() != null ? partido.getResultadoPartido().getGolesVisitante() : 0;
                    }

                    TablaPosicionDTO localDTO = mapaTabla.get(partido.getEquipoLocal().getId());
                    TablaPosicionDTO visitanteDTO = mapaTabla.get(partido.getEquipoVisitante().getId());

                    localDTO.setPartidosJugados(localDTO.getPartidosJugados() + 1);
                    visitanteDTO.setPartidosJugados(visitanteDTO.getPartidosJugados() + 1);

                    localDTO.setGolesAFavor(localDTO.getGolesAFavor() + golesLocal);
                    localDTO.setGolesEnContra(localDTO.getGolesEnContra() + golesVisitante);

                    visitanteDTO.setGolesAFavor(visitanteDTO.getGolesAFavor() + golesVisitante);
                    visitanteDTO.setGolesEnContra(visitanteDTO.getGolesEnContra() + golesLocal);

                    if (golesLocal > golesVisitante) {
                        localDTO.setPartidosGanados(localDTO.getPartidosGanados() + 1);
                        localDTO.setPuntos(localDTO.getPuntos() + torneo.getPuntosVictoria());

                        visitanteDTO.setPartidosPerdidos(visitanteDTO.getPartidosPerdidos() + 1);
                        visitanteDTO.setPuntos(visitanteDTO.getPuntos() + torneo.getPuntosDerrota());
                    } else if (golesLocal < golesVisitante) {
                        visitanteDTO.setPartidosGanados(visitanteDTO.getPartidosGanados() + 1);
                        visitanteDTO.setPuntos(visitanteDTO.getPuntos() + torneo.getPuntosVictoria());

                        localDTO.setPartidosPerdidos(localDTO.getPartidosPerdidos() + 1);
                        localDTO.setPuntos(localDTO.getPuntos() + torneo.getPuntosDerrota());
                    } else {
                        localDTO.setPartidosEmpatados(localDTO.getPartidosEmpatados() + 1);
                        localDTO.setPuntos(localDTO.getPuntos() + torneo.getPuntosEmpate());

                        visitanteDTO.setPartidosEmpatados(visitanteDTO.getPartidosEmpatados() + 1);
                        visitanteDTO.setPuntos(visitanteDTO.getPuntos() + torneo.getPuntosEmpate());
                    }

                    localDTO.setDiferenciaGoles(localDTO.getGolesAFavor() - localDTO.getGolesEnContra());
                    visitanteDTO.setDiferenciaGoles(visitanteDTO.getGolesAFavor() - visitanteDTO.getGolesEnContra());
                }
            }
        }

        return mapaTabla.values().stream()
                .sorted((a, b) -> {
                    int comparePuntos = b.getPuntos().compareTo(a.getPuntos());
                    if (comparePuntos != 0) return comparePuntos;
                    return b.getDiferenciaGoles().compareTo(a.getDiferenciaGoles());
                })
                .collect(Collectors.toList());
    }

    private Torneo getTorneoOrThrow(Long torneoId) {
        return torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con id: " + torneoId));
    }
}