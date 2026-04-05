package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.HistorialCampeon;

import java.util.List;

public interface HistorialCampeonService {

    // Registrar campeón al finalizar torneo
    HistorialCampeon registrarCampeon(Long torneoId, Long equipoId);

    // Listar historial por torneo
    List<HistorialCampeon> findByTorneo(Long torneoId);

    // Ranking histórico (más títulos)
    List<Object[]> rankingEquiposMasTitulos();
}