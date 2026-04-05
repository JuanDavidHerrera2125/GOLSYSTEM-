package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoFase;
import JUANDEV.PRO.GOLSYSTEM.model.*;
import JUANDEV.PRO.GOLSYSTEM.repository.FaseRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.PartidoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.PartidoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PartidoServiceImpl implements PartidoService {

    private final PartidoRepository partidoRepository;
    private final FaseRepository faseRepository;

    // Inyección por constructor (Práctica de producción)
    public PartidoServiceImpl(PartidoRepository partidoRepository, FaseRepository faseRepository) {
        this.partidoRepository = partidoRepository;
        this.faseRepository = faseRepository;
    }

    @Override
    public Partido save(Partido partido) {
        return partidoRepository.save(partido);
    }

    @Override
    public Optional<Partido> findById(Long id) {
        return partidoRepository.findById(id);
    }

    @Override
    public List<Partido> findAll() {
        return partidoRepository.findAll();
    }

    @Override
    public Partido update(Long id, Partido partido) {
        return partidoRepository.findById(id)
                .map(existing -> {
                    existing.setFecha(partido.getFecha());
                    existing.setHora(partido.getHora());
                    existing.setJornada(partido.getJornada());
                    existing.setEstado(partido.getEstado());
                    return partidoRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Partido no encontrado con id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado con id: " + id));

        Fase fase = partido.getFase();
        if (fase != null) {
            fase.getPartidos().remove(partido);
        }
        if (partido.getGrupo() != null) {
            partido.getGrupo().getPartidos().remove(partido);
        }
        partidoRepository.delete(partido);
    }

    @Override
    public long count() {
        return partidoRepository.count();
    }

    // ================= FIXTURES (ROUND-ROBIN) =================

    @Override
    public void generateFixtureLiga(Long faseId) {
        Fase fase = getFaseOrThrow(faseId);
        if (fase.getTipoFase() != TipoFase.LIGA) {
            throw new RuntimeException("La fase no es tipo LIGA");
        }
        List<Equipo> equipos = new ArrayList<>(fase.getTorneo().getEquipos());
        validarEquipos(equipos);

        generarCalendarioRoundRobin(equipos, fase, null);
        faseRepository.save(fase);
    }

    @Override
    public void generateFixtureGrupos(Long faseId) {
        Fase fase = getFaseOrThrow(faseId);
        if (fase.getTipoFase() != TipoFase.GRUPOS) {
            throw new RuntimeException("La fase no es tipo GRUPOS");
        }
        if (fase.getGrupos().isEmpty()) {
            throw new RuntimeException("La fase no tiene grupos configurados");
        }

        for (Grupo grupo : fase.getGrupos()) {
            List<Equipo> equipos = new ArrayList<>();
            for (GrupoEquipo ge : grupo.getEquipos()) {
                equipos.add(ge.getEquipo());
            }
            validarEquipos(equipos);
            generarCalendarioRoundRobin(equipos, fase, grupo);
        }
        faseRepository.save(fase);
    }

    @Override
    public void generateFixtureEliminatoria(Long faseId) {
        Fase fase = getFaseOrThrow(faseId);
        if (fase.getTipoFase() != TipoFase.ELIMINACION) {
            throw new RuntimeException("La fase no es tipo ELIMINACION");
        }
        List<Equipo> equipos = fase.getTorneo().getEquipos();
        validarEquipos(equipos);

        if (equipos.size() % 2 != 0) {
            throw new RuntimeException("Cantidad de equipos inválida para eliminatoria");
        }

        int jornada = 1;
        for (int i = 0; i < equipos.size(); i += 2) {
            Partido partido = new Partido();
            partido.setFase(fase);
            partido.setEquipoLocal(equipos.get(i));
            partido.setEquipoVisitante(equipos.get(i + 1));
            partido.setJornada(jornada++);
            fase.addPartido(partido);
        }
        faseRepository.save(fase);
    }

    // ================= ALGORITMO ROUND-ROBIN (SISTEMA BERGER) =================
    private void generarCalendarioRoundRobin(List<Equipo> equipos, Fase fase, Grupo grupo) {
        boolean esImpar = equipos.size() % 2 != 0;

        // Si es impar, añadimos un equipo fantasma (null) para balancear las parejas
        if (esImpar) {
            equipos.add(null);
        }

        int numEquipos = equipos.size();
        int numJornadas = numEquipos - 1;
        int partidosPorJornada = numEquipos / 2;

        for (int jornada = 0; jornada < numJornadas; jornada++) {
            for (int partidoIdx = 0; partidoIdx < partidosPorJornada; partidoIdx++) {

                int localIdx = (jornada + partidoIdx) % (numEquipos - 1);
                int visitanteIdx = (numEquipos - 1 - partidoIdx + jornada) % (numEquipos - 1);

                // El último equipo se queda fijo para la rotación
                if (partidoIdx == 0) {
                    visitanteIdx = numEquipos - 1;
                }

                Equipo local = equipos.get(localIdx);
                Equipo visitante = equipos.get(visitanteIdx);

                // Si alguno es null (fantasma), significa que el otro equipo descansa en esta jornada
                if (local != null && visitante != null) {
                    Partido partido = new Partido();
                    partido.setFase(fase);
                    partido.setGrupo(grupo);

                    // Alternar localías para que sea equitativo
                    if (jornada % 2 == 0) {
                        partido.setEquipoLocal(local);
                        partido.setEquipoVisitante(visitante);
                    } else {
                        partido.setEquipoLocal(visitante);
                        partido.setEquipoVisitante(local);
                    }

                    partido.setJornada(jornada + 1);
                    fase.addPartido(partido);
                }
            }
        }
    }

    private Fase getFaseOrThrow(Long faseId) {
        return faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con id: " + faseId));
    }

    private void validarEquipos(List<Equipo> equipos) {
        if (equipos == null || equipos.isEmpty() || (equipos.size() < 2 && equipos.get(0) != null)) {
            throw new RuntimeException("No hay suficientes equipos para generar fixture");
        }
    }
}