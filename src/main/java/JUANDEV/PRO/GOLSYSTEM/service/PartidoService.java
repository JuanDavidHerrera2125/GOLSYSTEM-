package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Partido;

import java.util.List;
import java.util.Optional;

public interface PartidoService {

    // ================= CRUD =================

    Partido save(Partido partido);

    Optional<Partido> findById(Long id);

    List<Partido> findAll();

    Partido update(Long id, Partido partido);

    void deleteById(Long id);

    long count();

    // ================= LÓGICA =================

    // Liga (todos contra todos)
    void generateFixtureLiga(Long faseId);

    // Fase de grupos
    void generateFixtureGrupos(Long faseId);

    // Eliminatoria directa
    void generateFixtureEliminatoria(Long faseId);
}