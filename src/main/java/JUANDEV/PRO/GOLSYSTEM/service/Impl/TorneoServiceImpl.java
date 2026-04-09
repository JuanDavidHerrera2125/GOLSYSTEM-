package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.dto.TablaPosicionDTO;
import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import JUANDEV.PRO.GOLSYSTEM.enums.EstadoTorneo;
import JUANDEV.PRO.GOLSYSTEM.enums.TipoFase;
import JUANDEV.PRO.GOLSYSTEM.model.Equipo;
import JUANDEV.PRO.GOLSYSTEM.model.Fase;
import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.model.Torneo;
import JUANDEV.PRO.GOLSYSTEM.repository.EquipoRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.TorneoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.FaseService;
import JUANDEV.PRO.GOLSYSTEM.service.PartidoService;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import JUANDEV.PRO.GOLSYSTEM.service.TorneoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TorneoServiceImpl implements TorneoService {

    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository; // Inyectado para inscripciones
    private final FaseService faseService;
    private final PartidoService partidoService;
    private final TablaPosicionService tablaService;

    public TorneoServiceImpl(TorneoRepository torneoRepository,
                             EquipoRepository equipoRepository,
                             FaseService faseService,
                             PartidoService partidoService,
                             TablaPosicionService tablaService) {
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
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
        Torneo torneo = getTorneoOrThrow(id);
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
    public void enrollTeam(Long torneoId, Long equipoId) {
        Torneo torneo = getTorneoOrThrow(torneoId);
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con id: " + equipoId));

        // Validamos que no esté ya inscrito
        if (torneo.getEquipos().contains(equipo)) {
            throw new RuntimeException("El equipo '" + equipo.getNombre() + "' ya está inscrito en este torneo.");
        }

        // Usamos el helper de la entidad para mantener la integridad bidireccional
        torneo.addEquipo(equipo);
        torneoRepository.save(torneo);
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
            throw new RuntimeException("El torneo no está listo para iniciar (estado actual: " + torneo.getEstado() + ")");
        }
        torneo.setEstado(EstadoTorneo.EN_CURSO);
        torneoRepository.save(torneo);
    }

    @Override
    public void finishTorneo(Long torneoId) {
        Torneo torneo = getTorneoOrThrow(torneoId);
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
                if (jornadaLimite != null && partido.getJornada() > jornadaLimite) continue;

                EstadoPartido est = partido.getEstado();
                if (est == EstadoPartido.FINALIZADO || est == EstadoPartido.WALKOVER) {
                    int golesLocal = 0, golesVisitante = 0;

                    if (partido.getResultadoPartido() != null) {
                        golesLocal = Optional.ofNullable(partido.getResultadoPartido().getGolesLocal()).orElse(0);
                        golesVisitante = Optional.ofNullable(partido.getResultadoPartido().getGolesVisitante()).orElse(0);
                    }

                    TablaPosicionDTO localDTO = mapaTabla.get(partido.getEquipoLocal().getId());
                    TablaPosicionDTO visitanteDTO = mapaTabla.get(partido.getEquipoVisitante().getId());

                    actualizarDTO(localDTO, golesLocal, golesVisitante, torneo, true);
                    actualizarDTO(visitanteDTO, golesVisitante, golesLocal, torneo, false);
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

    private void actualizarDTO(TablaPosicionDTO dto, int favor, int contra, Torneo torneo, boolean esLocal) {
        dto.setPartidosJugados(dto.getPartidosJugados() + 1);
        dto.setGolesAFavor(dto.getGolesAFavor() + favor);
        dto.setGolesEnContra(dto.getGolesEnContra() + contra);
        dto.setDiferenciaGoles(dto.getGolesAFavor() - dto.getGolesEnContra());

        if (favor > contra) {
            dto.setPartidosGanados(dto.getPartidosGanados() + 1);
            dto.setPuntos(dto.getPuntos() + torneo.getPuntosVictoria());
        } else if (favor < contra) {
            dto.setPartidosPerdidos(dto.getPartidosPerdidos() + 1);
            dto.setPuntos(dto.getPuntos() + torneo.getPuntosDerrota());
        } else {
            dto.setPartidosEmpatados(dto.getPartidosEmpatados() + 1);
            dto.setPuntos(dto.getPuntos() + torneo.getPuntosEmpate());
        }
    }

    private Torneo getTorneoOrThrow(Long torneoId) {
        return torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con id: " + torneoId));
    }
}