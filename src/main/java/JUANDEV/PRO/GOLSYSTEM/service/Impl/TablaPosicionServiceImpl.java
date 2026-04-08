package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import JUANDEV.PRO.GOLSYSTEM.enums.TipoTarjeta;
import JUANDEV.PRO.GOLSYSTEM.model.*;
import JUANDEV.PRO.GOLSYSTEM.repository.PartidoRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.PosicionEquipoRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.TablaPosicionRepository;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del motor de lógica para la tabla de posiciones del GOLSYSTEM.
 * Diseñado para ser idempotente y autónomo.
 */
@Service
@Transactional
public class TablaPosicionServiceImpl implements TablaPosicionService {

    private final TablaPosicionRepository tablaPosicionRepository;
    private final PartidoRepository partidoRepository;
    private final PosicionEquipoRepository posicionEquipoRepository;

    public TablaPosicionServiceImpl(TablaPosicionRepository tablaPosicionRepository,
                                    PartidoRepository partidoRepository,
                                    PosicionEquipoRepository posicionEquipoRepository) {
        this.tablaPosicionRepository = tablaPosicionRepository;
        this.partidoRepository = partidoRepository;
        this.posicionEquipoRepository = posicionEquipoRepository;
    }

    // ================= CRUD BÁSICO =================
    @Override public TablaPosicion save(TablaPosicion tabla) { return tablaPosicionRepository.save(tabla); }
    @Override public Optional<TablaPosicion> findById(Long id) { return tablaPosicionRepository.findById(id); }
    @Override public List<TablaPosicion> findAll() { return tablaPosicionRepository.findAll(); }
    @Override public long count() { return tablaPosicionRepository.count(); }
    @Override public void deleteById(Long id) { tablaPosicionRepository.deleteById(id); }

    @Override
    public TablaPosicion update(Long id, TablaPosicion tabla) {
        return tablaPosicionRepository.findById(id).map(existing -> {
            existing.setFase(tabla.getFase());
            existing.setGrupo(tabla.getGrupo());
            return tablaPosicionRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Tabla no encontrada"));
    }

    // ================= MOTOR DE LOGICA AUTÓNOMA =================

    @Override
    public void updateTablaFromPartido(Long partidoId) {
        // 1. Cargar el partido y validar que tenga resultado
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        ResultadoPartido resultado = partido.getResultadoPartido();
        if (resultado == null) return;

        // 2. Obtener o AUTO-CREAR la infraestructura de la tabla
        TablaPosicion tabla = obtenerTablaOAutoCrear(partido);

        // 3. Obtener o AUTO-VINCULAR equipos a la tabla
        PosicionEquipo local = getOrCreatePosicion(tabla, partido.getEquipoLocal());
        PosicionEquipo visitante = getOrCreatePosicion(tabla, partido.getEquipoVisitante());

        int gl = Optional.ofNullable(resultado.getGolesLocal()).orElse(0);
        int gv = Optional.ofNullable(resultado.getGolesVisitante()).orElse(0);

        // 4. Actualizar Estadísticas de Juego (Suma dinámica)
        local.setPj(local.getPj() + 1);
        visitante.setPj(visitante.getPj() + 1);
        local.setGf(local.getGf() + gl);
        local.setGc(local.getGc() + gv);
        visitante.setGf(visitante.getGf() + gv);
        visitante.setGc(visitante.getGc() + gl);
        local.setDg(local.getGf() - local.getGc());
        visitante.setDg(visitante.getGf() - visitante.getGc());

        // 5. Distribución de Puntos
        int puntosVictoria = (partido.getFase() != null && partido.getFase().getTorneo() != null)
                ? partido.getFase().getTorneo().getPuntosVictoria() : 3;
        int puntosEmpate = (partido.getFase() != null && partido.getFase().getTorneo() != null)
                ? partido.getFase().getTorneo().getPuntosEmpate() : 1;

        if (gl > gv) {
            local.setPg(local.getPg() + 1);
            local.setPuntos(local.getPuntos() + puntosVictoria);
            visitante.setPp(visitante.getPp() + 1);
        } else if (gl < gv) {
            visitante.setPg(visitante.getPg() + 1);
            visitante.setPuntos(visitante.getPuntos() + puntosVictoria);
            local.setPp(local.getPp() + 1);
        } else {
            local.setPe(local.getPe() + 1);
            visitante.setPe(visitante.getPe() + 1);
            local.setPuntos(local.getPuntos() + puntosEmpate);
            visitante.setPuntos(visitante.getPuntos() + puntosEmpate);
        }

        // 6. Disciplina
        actualizarDisciplina(partido, local, visitante);

        // 7. Persistencia
        posicionEquipoRepository.save(local);
        posicionEquipoRepository.save(visitante);
    }

    private void actualizarDisciplina(Partido partido, PosicionEquipo local, PosicionEquipo visitante) {
        if (partido.getTarjetas() == null) return;
        for (EventoTarjeta t : partido.getTarjetas()) {
            boolean esLocal = t.getJugador().getEquipo().getId().equals(local.getEquipo().getId());
            if (t.getTipoTarjeta() == TipoTarjeta.AMARILLA) {
                if (esLocal) local.setAmarillas(local.getAmarillas() + 1);
                else visitante.setAmarillas(visitante.getAmarillas() + 1);
            } else {
                if (esLocal) local.setRojas(local.getRojas() + 1);
                else visitante.setRojas(visitante.getRojas() + 1);
            }
        }
    }

    @Override
    public void ordenarTabla(Long tablaId) {
        TablaPosicion tabla = tablaPosicionRepository.findById(tablaId)
                .orElseThrow(() -> new RuntimeException("Tabla no encontrada"));

        List<PosicionEquipo> posiciones = tabla.getPosiciones();

        // Criterios FIFA: Puntos -> DG -> GF -> Menos Rojas -> Menos Amarillas
        posiciones.sort(
                Comparator.comparing(PosicionEquipo::getPuntos).reversed()
                        .thenComparing(PosicionEquipo::getDg).reversed()
                        .thenComparing(PosicionEquipo::getGf).reversed()
                        .thenComparing(PosicionEquipo::getRojas)
                        .thenComparing(PosicionEquipo::getAmarillas)
        );

        int rnk = 1;
        for (PosicionEquipo p : posiciones) {
            p.setPosicion(rnk++);
        }
        posicionEquipoRepository.saveAll(posiciones);
    }

    @Override
    @Transactional
    public void recalculateTabla(Long tablaId) {
        TablaPosicion tabla = tablaPosicionRepository.findById(tablaId)
                .orElseThrow(() -> new RuntimeException("Tabla no encontrada"));

        // 1. Limpieza total de posiciones previas
        posicionEquipoRepository.deleteAll(tabla.getPosiciones());
        tabla.getPosiciones().clear();
        tablaPosicionRepository.saveAndFlush(tabla);

        // 2. BUSQUEDA SIMPLIFICADA (Para forzar el arrastre de datos)
        // Vamos a buscar todos los partidos de la FASE, ignorando el filtro de Grupo por ahora
        List<Partido> partidos = partidoRepository.findByFaseIdAndEstado(
                tabla.getFase().getId(),
                EstadoPartido.FINALIZADO
        );

        // LOG DE CONTROL: Mira tu consola de IntelliJ para ver si este número es > 0
        System.out.println("Partidos encontrados para procesar: " + partidos.size());

        // 3. Procesar y Guardar
        partidos.forEach(p -> this.updateTablaFromPartido(p.getId()));

        // 4. Ordenar y Sincronizar con la DB
        this.ordenarTabla(tablaId);
        posicionEquipoRepository.saveAllAndFlush(tabla.getPosiciones());
    }

    // ================= HELPERS DE INFRAESTRUCTURA AUTOMÁTICA =================

    private TablaPosicion obtenerTablaOAutoCrear(Partido partido) {
        if (partido.getGrupo() != null) {
            return tablaPosicionRepository.findByGrupoId(partido.getGrupo().getId())
                    .orElseGet(() -> {
                        TablaPosicion t = new TablaPosicion();
                        t.setGrupo(partido.getGrupo());
                        t.setFase(partido.getFase());
                        return tablaPosicionRepository.save(t);
                    });
        }
        return tablaPosicionRepository.findByFaseIdAndGrupoIsNull(partido.getFase().getId())
                .orElseGet(() -> {
                    TablaPosicion t = new TablaPosicion();
                    t.setFase(partido.getFase());
                    return tablaPosicionRepository.save(t);
                });
    }

    private PosicionEquipo getOrCreatePosicion(TablaPosicion tabla, Equipo equipo) {
        return tabla.getPosiciones().stream()
                .filter(p -> p.getEquipo().getId().equals(equipo.getId()))
                .findFirst()
                .orElseGet(() -> {
                    PosicionEquipo nuevaPos = new PosicionEquipo();
                    nuevaPos.setTabla(tabla);
                    nuevaPos.setEquipo(equipo);
                    nuevaPos.setPj(0); nuevaPos.setPg(0); nuevaPos.setPe(0); nuevaPos.setPp(0);
                    nuevaPos.setGf(0); nuevaPos.setGc(0); nuevaPos.setDg(0);
                    nuevaPos.setPuntos(0); nuevaPos.setAmarillas(0); nuevaPos.setRojas(0);
                    // IMPORTANTE: Guardamos y añadimos a la lista para que el Stream lo vea en la próxima vuelta
                    PosicionEquipo saved = posicionEquipoRepository.save(nuevaPos);
                    tabla.getPosiciones().add(saved);
                    return saved;
                });
    }
}