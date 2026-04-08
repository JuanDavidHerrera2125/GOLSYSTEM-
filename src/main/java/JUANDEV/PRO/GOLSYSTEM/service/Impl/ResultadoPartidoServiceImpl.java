package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import JUANDEV.PRO.GOLSYSTEM.enums.TipoTarjeta;
import JUANDEV.PRO.GOLSYSTEM.model.*;
import JUANDEV.PRO.GOLSYSTEM.repository.*;
import JUANDEV.PRO.GOLSYSTEM.service.ResultadoPartidoService;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResultadoPartidoServiceImpl implements ResultadoPartidoService {

    private final PartidoRepository partidoRepository;
    private final TablaPosicionService tablaPosicionService;
    private final JugadorRepository jugadorRepository;
    private final EventoGolRepository eventoGolRepository;
    private final EventoTarjetaRepository eventoTarjetaRepository;

    public ResultadoPartidoServiceImpl(PartidoRepository partidoRepository,
                                       TablaPosicionService tablaPosicionService,
                                       JugadorRepository jugadorRepository,
                                       EventoGolRepository eventoGolRepository,
                                       EventoTarjetaRepository eventoTarjetaRepository) {
        this.partidoRepository = partidoRepository;
        this.tablaPosicionService = tablaPosicionService;
        this.jugadorRepository = jugadorRepository;
        this.eventoGolRepository = eventoGolRepository;
        this.eventoTarjetaRepository = eventoTarjetaRepository;
    }

    @Override
    public void registrarGol(Long partidoId, Long jugadorId, Integer minuto, Boolean esPenal, Boolean esAutogol) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        validarPartidoEnJuego(partido);

        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        // 1. Manejo del Resultado (Garantizar identidad)
        ResultadoPartido res = partido.getResultadoPartido();
        if (res == null) {
            res = new ResultadoPartido();
            res.setPartido(partido);
            res.setId(partidoId); // Sincronización @MapsId
            res.setGolesLocal(0);
            res.setGolesVisitante(0);
            partido.setResultadoPartido(res);

            // 🔥 CLAVE: Guardar y sincronizar el partido inmediatamente para dar identidad al resultado
            partido = partidoRepository.saveAndFlush(partido);
            res = partido.getResultadoPartido();
        }

        // 2. Lógica de marcador
        boolean esLocal = jugador.getEquipo().getId().equals(partido.getEquipoLocal().getId());
        if (Boolean.TRUE.equals(esAutogol)) {
            if (esLocal) res.setGolesVisitante(res.getGolesVisitante() + 1);
            else res.setGolesLocal(res.getGolesLocal() + 1);
        } else {
            if (esLocal) res.setGolesLocal(res.getGolesLocal() + 1);
            else res.setGolesVisitante(res.getGolesVisitante() + 1);
        }

        // 3. Registro de evento estadístico
        EventoGol gol = new EventoGol();
        gol.setPartido(partido);
        gol.setJugador(jugador);
        gol.setMinuto(minuto);
        gol.setEsPenal(esPenal);
        gol.setEsAutogol(esAutogol);

        eventoGolRepository.save(gol);
        partidoRepository.save(partido); // Actualiza el marcador por cascada
    }

    @Override
    public void registrarTarjeta(Long partidoId, Long jugadorId, TipoTarjeta tipoTarjeta, Integer minuto) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        validarPartidoEnJuego(partido);

        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        EventoTarjeta tarjeta = new EventoTarjeta();
        tarjeta.setPartido(partido);
        tarjeta.setJugador(jugador);
        tarjeta.setTipoTarjeta(tipoTarjeta);
        tarjeta.setMinuto(minuto);

        eventoTarjetaRepository.save(tarjeta);
    }

    @Override
    public void cerrarPartido(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        if (partido.getEstado() == EstadoPartido.FINALIZADO) {
            throw new RuntimeException("El partido ya se encuentra finalizado");
        }

        // Aseguramos que el objeto Resultado no sea nulo
        if (partido.getResultadoPartido() == null) {
            ResultadoPartido sinGoles = new ResultadoPartido();
            sinGoles.setPartido(partido);
            sinGoles.setId(partidoId);
            sinGoles.setGolesLocal(0);
            sinGoles.setGolesVisitante(0);
            partido.setResultadoPartido(sinGoles);
        }

        partido.setEstado(EstadoPartido.FINALIZADO);

        // 🔥 CLAVE 1: saveAndFlush asegura que los cambios estén en la DB
        // antes de que el servicio de tabla intente leerlos.
        partidoRepository.saveAndFlush(partido);

        // 🔥 CLAVE 2: Pasar el objeto completo si es posible, o asegurar la fase.
        // El motor necesita saber a qué TABLA de qué FASE pertenece el partido.
        tablaPosicionService.updateTablaFromPartido(partidoId);
    }

    @Override
    public ResultadoPartido registrarResultado(Long partidoId, ResultadoPartido resultado) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        validarPartidoEnJuego(partido);

        resultado.setPartido(partido);
        resultado.setId(partidoId);
        partido.setResultadoPartido(resultado);
        partidoRepository.save(partido);
        return resultado;
    }

    private void validarPartidoEnJuego(Partido partido) {
        if (partido.getEstado() == EstadoPartido.FINALIZADO ||
                partido.getEstado() == EstadoPartido.WALKOVER) {
            throw new RuntimeException("Acción no permitida: El partido ya está cerrado.");
        }
    }
}