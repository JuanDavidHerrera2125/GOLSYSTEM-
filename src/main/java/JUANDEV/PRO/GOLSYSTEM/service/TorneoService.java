package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Torneo;

import java.util.List;
import java.util.Optional;

public interface TorneoService {

    // ================= CRUD =================

    Torneo save(Torneo torneo);

    Optional<Torneo> findById(Long id);

    List<Torneo> findAll();

    Torneo update(Long id, Torneo torneo);

    void deleteById(Long id);

    long count();

    // ================= FLUJO DEL TORNEO =================

    // Crear torneo en estado CONFIGURACION
    Torneo createTorneo(Torneo torneo);

    // Generar todo el sistema (fases + fixture)
    void generateTorneo(Long torneoId);

    // Iniciar torneo (EN_CURSO)
    void startTorneo(Long torneoId);

    // Finalizar torneo
    void finishTorneo(Long torneoId);

    // Archivar torneo
    void archiveTorneo(Long torneoId);
}