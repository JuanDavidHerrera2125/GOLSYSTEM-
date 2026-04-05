package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Fase;

import java.util.List;
import java.util.Optional;

public interface FaseService {

    // ================= CRUD =================

    Fase save(Fase fase);

    Optional<Fase> findById(Long id);

    List<Fase> findAll();

    Fase update(Fase fase, Long id);

    void deleteById(Long id);

    long count();

    // ================= LÓGICA DE TORNEO =================

    // Crear fase dentro del torneo
    Fase createFaseInTorneo(Long torneoId, Fase fase);

    // Asignar equipos a la fase (liga o base para grupos)
    void assignEquiposToFase(Long faseId, List<Long> equiposIds);

    // Generar grupos automáticamente
    void generateGruposInFase(Long faseId, int numeroGrupos);

    // Crear tabla de posiciones
    void createTablaForFase(Long faseId);
}