package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.Jugador;
import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.model.Suspension;
import JUANDEV.PRO.GOLSYSTEM.repository.JugadorRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.PartidoRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.SuspensionRepository;
import JUANDEV.PRO.GOLSYSTEM.service.SuspensionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SuspensionServiceImpl implements SuspensionService {

    private final SuspensionRepository suspensionRepository;
    private final JugadorRepository jugadorRepository;
    private final PartidoRepository partidoRepository;

    public SuspensionServiceImpl(SuspensionRepository suspensionRepository,
                                 JugadorRepository jugadorRepository,
                                 PartidoRepository partidoRepository) {
        this.suspensionRepository = suspensionRepository;
        this.jugadorRepository = jugadorRepository;
        this.partidoRepository = partidoRepository;
    }

    // ================= LÓGICA =================

    @Override
    public Suspension aplicarSuspension(Long jugadorId, Integer partidos, String motivo, Long partidoId) {

        // 🔍 Validar jugador
        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con id: " + jugadorId));

        // 🔍 Validar partido (opcional)
        Partido partido = null;
        if (partidoId != null) {
            partido = partidoRepository.findById(partidoId)
                    .orElseThrow(() -> new RuntimeException("Partido no encontrado con id: " + partidoId));
        }

        // ⚠️ Validación: no crear suspensión inválida
        if (partidos == null || partidos <= 0) {
            throw new RuntimeException("La cantidad de partidos de suspensión debe ser mayor a 0");
        }

        // 🟥 Crear suspensión
        Suspension suspension = new Suspension();
        suspension.setJugador(jugador);
        suspension.setPartidosSuspendido(partidos);
        suspension.setMotivo(motivo);
        suspension.setActiva(true);
        suspension.setPartidoOrigen(partido);

        return suspensionRepository.save(suspension);
    }

    @Override
    public void descontarPartidoSuspension(Long jugadorId) {

        List<Suspension> suspensiones = suspensionRepository
                .findByJugadorIdAndActivaTrue(jugadorId);

        for (Suspension s : suspensiones) {

            // 🔽 Reducir contador
            s.setPartidosSuspendido(s.getPartidosSuspendido() - 1);

            // ✅ Si ya cumplió suspensión → desactivar
            if (s.getPartidosSuspendido() <= 0) {
                s.setActiva(false);
            }
        }
    }

    @Override
    public boolean jugadorHabilitado(Long jugadorId) {

        // 🔍 Si tiene suspensiones activas → NO puede jugar
        return suspensionRepository
                .findByJugadorIdAndActivaTrue(jugadorId)
                .isEmpty();
    }

    @Override
    public List<Suspension> obtenerSuspensionesActivas(Long jugadorId) {

        return suspensionRepository.findByJugadorIdAndActivaTrue(jugadorId);
    }
}