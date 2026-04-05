package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.*;
import JUANDEV.PRO.GOLSYSTEM.repository.PartidoRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.TablaPosicionRepository;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TablaPosicionServiceImpl implements TablaPosicionService {

    private final TablaPosicionRepository tablaPosicionRepository;
    private final PartidoRepository partidoRepository;

    public TablaPosicionServiceImpl(TablaPosicionRepository tablaPosicionRepository,
                                    PartidoRepository partidoRepository) {
        this.tablaPosicionRepository = tablaPosicionRepository;
        this.partidoRepository = partidoRepository;
    }

    // ================= CRUD =================

    @Override
    public TablaPosicion save(TablaPosicion tabla) {
        return tablaPosicionRepository.save(tabla);
    }

    @Override
    public Optional<TablaPosicion> findById(Long id) {
        return tablaPosicionRepository.findById(id);
    }

    @Override
    public List<TablaPosicion> findAll() {
        return tablaPosicionRepository.findAll();
    }

    @Override
    public TablaPosicion update(Long id, TablaPosicion tabla) {
        return tablaPosicionRepository.findById(id)
                .map(existing -> {
                    existing.setFase(tabla.getFase());
                    existing.setGrupo(tabla.getGrupo());
                    return tablaPosicionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Tabla no encontrada con id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        TablaPosicion tabla = tablaPosicionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tabla no encontrada con id: " + id));

        tablaPosicionRepository.delete(tabla);
    }

    @Override
    public long count() {
        return tablaPosicionRepository.count();
    }

    // ================= LÓGICA REAL =================

    @Override
    public void updateTablaFromPartido(Long partidoId) {

        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado con id: " + partidoId));

        ResultadoPartido resultado = partido.getResultadoPartido();

        if (resultado == null) {
            throw new RuntimeException("El partido no tiene resultado registrado");
        }

        // 🔍 Obtener tabla correcta
        TablaPosicion tabla = obtenerTabla(partido);

        PosicionEquipo local = getPosicion(tabla, partido.getEquipoLocal());
        PosicionEquipo visitante = getPosicion(tabla, partido.getEquipoVisitante());

        int gl = Optional.ofNullable(resultado.getGolesLocal()).orElse(0);
        int gv = Optional.ofNullable(resultado.getGolesVisitante()).orElse(0);

        // ================= ESTADÍSTICAS =================
        local.setPj(local.getPj() + 1);
        visitante.setPj(visitante.getPj() + 1);

        local.setGf(local.getGf() + gl);
        local.setGc(local.getGc() + gv);

        visitante.setGf(visitante.getGf() + gv);
        visitante.setGc(visitante.getGc() + gl);

        local.setDg(local.getGf() - local.getGc());
        visitante.setDg(visitante.getGf() - visitante.getGc());

        if (gl > gv) {
            local.setPg(local.getPg() + 1);
            local.setPuntos(local.getPuntos() + 3);
            visitante.setPp(visitante.getPp() + 1);
        } else if (gl < gv) {
            visitante.setPg(visitante.getPg() + 1);
            visitante.setPuntos(visitante.getPuntos() + 3);
            local.setPp(local.getPp() + 1);
        } else {
            local.setPe(local.getPe() + 1);
            visitante.setPe(visitante.getPe() + 1);
            local.setPuntos(local.getPuntos() + 1);
            visitante.setPuntos(visitante.getPuntos() + 1);
        }

        // Se ordena y se guarda internamente aquí adentro
        ordenarTabla(tabla.getId());
    }

    @Override
    public void recalculateTabla(Long tablaId) {

        TablaPosicion tabla = tablaPosicionRepository.findById(tablaId)
                .orElseThrow(() -> new RuntimeException("Tabla no encontrada con id: " + tablaId));

        for (PosicionEquipo pos : tabla.getPosiciones()) {
            pos.setPj(0);
            pos.setPg(0);
            pos.setPe(0);
            pos.setPp(0);
            pos.setGf(0);
            pos.setGc(0);
            pos.setDg(0);
            pos.setPuntos(0);
            pos.setPosicion(0);
        }

        tablaPosicionRepository.save(tabla);
    }

    @Override
    public void ordenarTabla(Long tablaId) {

        TablaPosicion tabla = tablaPosicionRepository.findById(tablaId)
                .orElseThrow(() -> new RuntimeException("Tabla no encontrada con id: " + tablaId));

        List<PosicionEquipo> posiciones = tabla.getPosiciones();

        posiciones.sort(
                Comparator.comparing(PosicionEquipo::getPuntos, Comparator.nullsLast(Integer::compareTo)).reversed()
                        .thenComparing(PosicionEquipo::getDg, Comparator.nullsLast(Integer::compareTo)).reversed()
                        .thenComparing(PosicionEquipo::getGf, Comparator.nullsLast(Integer::compareTo)).reversed()
        );

        int posicion = 1;
        for (PosicionEquipo p : posiciones) {
            p.setPosicion(posicion++);
        }

        tablaPosicionRepository.save(tabla);
    }

    // ================= HELPERS =================

    private TablaPosicion obtenerTabla(Partido partido) {
        if (partido.getGrupo() != null) {
            return tablaPosicionRepository.findByGrupoId(partido.getGrupo().getId())
                    .orElseThrow(() -> new RuntimeException("Tabla de grupo no encontrada"));
        }

        return tablaPosicionRepository.findByFaseIdAndGrupoIsNull(partido.getFase().getId())
                .orElseThrow(() -> new RuntimeException("Tabla general no encontrada"));
    }

    private PosicionEquipo getPosicion(TablaPosicion tabla, Equipo equipo) {
        return tabla.getPosiciones().stream()
                .filter(p -> p.getEquipo().getId().equals(equipo.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado en la tabla"));
    }
}