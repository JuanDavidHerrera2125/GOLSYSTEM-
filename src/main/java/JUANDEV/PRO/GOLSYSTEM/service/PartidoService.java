package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import java.util.List;
import java.util.Optional;

public interface PartidoService {

    // ================= CRUD BÁSICO =================
    Partido save(Partido partido);
    Optional<Partido> findById(Long id);
    List<Partido> findAll();
    Partido update(Long id, Partido partido);
    void deleteById(Long id);
    long count();

    // ================= LÓGICA DE TORNEO =================

    // Gestión de Estados (Crucial para la App)
    void cambiarEstado(Long id, EstadoPartido nuevoEstado);

    // Casos Especiales (Walkover / Escritorio)
    void registrarWalkover(Long partidoId, Long equipoGanadorId);

    // Consultas Especializadas
    List<Partido> obtenerPartidosPorEstado(EstadoPartido estado);

    // ================= GENERACIÓN DE FIXTURES =================
    void generateFixtureLiga(Long faseId);
    void generateFixtureGrupos(Long faseId);
    void generateFixtureEliminatoria(Long faseId);
}