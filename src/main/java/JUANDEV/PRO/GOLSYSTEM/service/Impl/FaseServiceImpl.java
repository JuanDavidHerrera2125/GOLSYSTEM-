package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoFase;
import JUANDEV.PRO.GOLSYSTEM.model.*;
import JUANDEV.PRO.GOLSYSTEM.repository.*;
import JUANDEV.PRO.GOLSYSTEM.service.FaseService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FaseServiceImpl implements FaseService {

    private final FaseRepository faseRepository;
    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final TablaPosicionRepository tablaPosicionRepository;

    public FaseServiceImpl(FaseRepository faseRepository,
                           TorneoRepository torneoRepository,
                           EquipoRepository equipoRepository,
                           TablaPosicionRepository tablaPosicionRepository) {
        this.faseRepository = faseRepository;
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
        this.tablaPosicionRepository = tablaPosicionRepository;
    }

    // ================= CRUD =================

    @Override
    public Fase save(Fase fase) {
        return faseRepository.save(fase);
    }

    @Override
    public Optional<Fase> findById(Long id) {
        return faseRepository.findById(id);
    }

    @Override
    public List<Fase> findAll() {
        return faseRepository.findAll();
    }

    @Override
    public Fase update(Fase fase, Long id) {
        return faseRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(fase.getNombre());
                    existing.setOrden(fase.getOrden());
                    existing.setTipoFase(fase.getTipoFase());
                    return faseRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Fase no encontrada"));
    }

    @Override
    public void deleteById(Long id) {
        Fase fase = faseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada"));

        if (fase.getTorneo() != null) {
            fase.getTorneo().getFases().remove(fase);
        }

        faseRepository.delete(fase);
    }

    @Override
    public long count() {
        return faseRepository.count();
    }

    // ================= LÓGICA REAL =================

    @Override
    public Fase createFaseInTorneo(Long torneoId, Fase fase) {

        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        if (!torneo.getEstado().name().equals("CONFIGURACION")) {
            throw new RuntimeException("No se puede modificar el torneo");
        }

        torneo.addFase(fase);

        return faseRepository.save(fase);
    }

    // 🔥 Asignar equipos a la fase (base para fixture o grupos)
    @Override
    public void assignEquiposToFase(Long faseId, List<Long> equiposIds) {

        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada"));

        if (!fase.getTorneo().getEstado().name().equals("CONFIGURACION")) {
            throw new RuntimeException("No se pueden modificar equipos");
        }

        List<Equipo> equipos = equipoRepository.findAllById(equiposIds);

        if (equipos.size() != equiposIds.size()) {
            throw new RuntimeException("Algunos equipos no existen");
        }

        // ⚠️ No se guardan en fase directamente (depende del tipo)
        // En GRUPOS → se asignan a Grupo
        // En LIGA → se usan directamente desde torneo

        // Validación básica
        if (equipos.isEmpty()) {
            throw new RuntimeException("Debe haber equipos para la fase");
        }
    }

    // 🔥 Generar grupos automáticamente
    @Override
    public void generateGruposInFase(Long faseId, int numeroGrupos) {

        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada"));

        if (fase.getTipoFase() == TipoFase.ELIMINACION) {
            throw new RuntimeException("No aplica grupos en eliminatoria");
        }

        List<Equipo> equipos = fase.getTorneo().getEquipos();

        if (equipos.size() < numeroGrupos) {
            throw new RuntimeException("Equipos insuficientes");
        }

        Collections.shuffle(equipos);

        // Crear grupos
        for (int i = 0; i < numeroGrupos; i++) {
            Grupo grupo = new Grupo();
            grupo.setNombre("Grupo " + (char) ('A' + i));
            fase.addGrupo(grupo);
        }

        // Distribuir equipos
        int index = 0;
        for (Equipo equipo : equipos) {

            Grupo grupo = fase.getGrupos().get(index % numeroGrupos);

            GrupoEquipo ge = new GrupoEquipo();
            ge.setEquipo(equipo);

            grupo.addEquipo(ge);

            index++;
        }

        faseRepository.save(fase);
    }

    // 🔥 Crear tabla de posiciones
    @Override
    public void createTablaForFase(Long faseId) {

        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada"));

        if (!fase.getGrupos().isEmpty()) {

            // TABLAS POR GRUPO
            for (Grupo grupo : fase.getGrupos()) {

                TablaPosicion tabla = new TablaPosicion();
                tabla.setFase(fase);
                tabla.setGrupo(grupo);

                for (GrupoEquipo ge : grupo.getEquipos()) {
                    PosicionEquipo pos = new PosicionEquipo();
                    pos.setEquipo(ge.getEquipo());
                    tabla.addPosicion(pos);
                }

                tablaPosicionRepository.save(tabla);
            }

        } else {

            // TABLA GENERAL (LIGA)
            TablaPosicion tabla = new TablaPosicion();
            tabla.setFase(fase);

            for (Equipo equipo : fase.getTorneo().getEquipos()) {
                PosicionEquipo pos = new PosicionEquipo();
                pos.setEquipo(equipo);
                tabla.addPosicion(pos);
            }

            tablaPosicionRepository.save(tabla);
        }
    }
}