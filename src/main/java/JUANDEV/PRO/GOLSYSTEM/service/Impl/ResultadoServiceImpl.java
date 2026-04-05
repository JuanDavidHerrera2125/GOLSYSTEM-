package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import JUANDEV.PRO.GOLSYSTEM.enums.TipoTarjeta;
import JUANDEV.PRO.GOLSYSTEM.model.*;
import JUANDEV.PRO.GOLSYSTEM.repository.JugadorRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.PartidoRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.ResultadoPartidoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.ResultadoService;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ResultadoServiceImpl implements ResultadoService {

    private final PartidoRepository partidoRepository;
    private final JugadorRepository jugadorRepository;
    private final ResultadoPartidoRepository resultadoRepository;
    private final TablaPosicionService tablaService;

    public ResultadoServiceImpl(PartidoRepository partidoRepository,
                                JugadorRepository jugadorRepository,
                                ResultadoPartidoRepository resultadoRepository,
                                TablaPosicionService tablaService) {
        this.partidoRepository = partidoRepository;
        this.jugadorRepository = jugadorRepository;
        this.resultadoRepository = resultadoRepository;
        this.tablaService = tablaService;
    }

    // ================= RESULTADO =================

    @Override
    public ResultadoPartido registrarResultado(Long partidoId, ResultadoPartido resultado) {

        Partido partido = getPartidoOrThrow(partidoId);

        if (partido.getEstado() == EstadoPartido.FINALIZADO) {
            throw new RuntimeException("El partido ya fue finalizado");
        }

        resultado.setPartido(partido);

        return resultadoRepository.save(resultado);
    }

    // ================= GOLES =================

    @Override
    public void registrarGol(Long partidoId, Long jugadorId, Integer minuto, Boolean esPenal, Boolean esAutogol) {

        Partido partido = getPartidoOrThrow(partidoId);
        Jugador jugador = getJugadorOrThrow(jugadorId);

        EventoGol gol = new EventoGol();
        gol.setPartido(partido);
        gol.setJugador(jugador);
        gol.setMinuto(minuto);
        gol.setEsPenal(esPenal != null ? esPenal : false);
        gol.setEsAutogol(esAutogol != null ? esAutogol : false);

        partido.addGol(gol);
    }

    // ================= TARJETAS =================

    @Override
    public void registrarTarjeta(Long partidoId, Long jugadorId, String tipoTarjeta, Integer minuto) {

        Partido partido = getPartidoOrThrow(partidoId);
        Jugador jugador = getJugadorOrThrow(jugadorId);

        EventoTarjeta tarjeta = new EventoTarjeta();
        tarjeta.setPartido(partido);
        tarjeta.setJugador(jugador);
        tarjeta.setMinuto(minuto);
        tarjeta.setTipoTarjeta(TipoTarjeta.valueOf(tipoTarjeta));

        partido.addTarjeta(tarjeta);
    }

    // ================= CIERRE =================

    @Override
    public void cerrarPartido(Long partidoId) {

        Partido partido = getPartidoOrThrow(partidoId);

        if (partido.getResultadoPartido() == null) {
            throw new RuntimeException("No se puede cerrar el partido sin resultado");
        }

        partido.setEstado(EstadoPartido.FINALIZADO);

        // 🔥 ACTUALIZA TABLA AUTOMÁTICAMENTE
        tablaService.updateTablaFromPartido(partidoId);
    }

    // ================= HELPERS =================

    private Partido getPartidoOrThrow(Long id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado con id: " + id));
    }

    private Jugador getJugadorOrThrow(Long id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con id: " + id));
    }
}