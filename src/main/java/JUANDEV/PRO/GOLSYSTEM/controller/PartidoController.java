package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.service.PartidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/partidos")
@RequiredArgsConstructor
public class PartidoController {

    private final PartidoService partidoService;

    // ================= CRUD BÁSICO =================

    @PostMapping
    public ResponseEntity<Partido> crear(@RequestBody Partido partido) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partidoService.save(partido));
    }

    @GetMapping
    public ResponseEntity<List<Partido>> listarTodos(
            @RequestParam(required = false) EstadoPartido estado) {
        if (estado != null) {
            return ResponseEntity.ok(partidoService.obtenerPartidosPorEstado(estado));
        }
        return ResponseEntity.ok(partidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partido> obtenerPorId(@PathVariable Long id) {
        return partidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Partido> actualizar(@PathVariable Long id, @RequestBody Partido partido) {
        return ResponseEntity.ok(partidoService.update(id, partido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        partidoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================= ACCIONES DE ESTADO =================

    // Cambiar estado (EN_JUEGO, FINALIZADO, etc.)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPartido nuevoEstado) {
        partidoService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok().build();
    }

    // Registrar un Walkover (Ganar por W)
    @PostMapping("/{id}/walkover")
    public ResponseEntity<Void> registrarWalkover(
            @PathVariable Long id,
            @RequestParam Long equipoGanadorId) {
        partidoService.registrarWalkover(id, equipoGanadorId);
        return ResponseEntity.ok().build();
    }

    // ================= GENERACIÓN DE FIXTURE =================

    @PostMapping("/fase/{faseId}/generar-liga")
    public ResponseEntity<Void> generarLiga(@PathVariable Long faseId) {
        partidoService.generateFixtureLiga(faseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}