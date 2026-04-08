package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.dto.TablaPosicionDTO;
import JUANDEV.PRO.GOLSYSTEM.model.Torneo;

import java.util.List;
import java.util.Optional;

public interface TorneoService {

    Torneo save(Torneo torneo);
    Optional<Torneo> findById(Long id);
    List<Torneo> findAll();
    Torneo update(Long id, Torneo torneo);
    void deleteById(Long id);
    long count();

    Torneo createTorneo(Torneo torneo);
    void generateTorneo(Long torneoId);
    void startTorneo(Long torneoId);
    void finishTorneo(Long torneoId);
    void archiveTorneo(Long torneoId);

    // 🌟 NUEVO MÉTODO PARA RELACIÓN MANY-TO-MANY
    void enrollTeam(Long torneoId, Long equipoId);

    List<TablaPosicionDTO> calcularTablaAcumulada(Long torneoId);
    List<TablaPosicionDTO> calcularTablaHastaJornada(Long torneoId, Integer jornada);
}