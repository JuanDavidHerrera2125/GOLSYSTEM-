package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Suspension;

import java.util.List;

public interface SuspensionService {

    // Aplicar suspensión a un jugador
    Suspension aplicarSuspension(Long jugadorId, Integer partidos, String motivo, Long partidoId);

    // Descontar partidos de suspensión después de un partido jugado
    void descontarPartidoSuspension(Long jugadorId);

    // Validar si el jugador puede jugar
    boolean jugadorHabilitado(Long jugadorId);

    // Obtener suspensiones activas de un jugador
    List<Suspension> obtenerSuspensionesActivas(Long jugadorId);
    List<Suspension> findAll();
    Suspension save(Suspension suspension);
}