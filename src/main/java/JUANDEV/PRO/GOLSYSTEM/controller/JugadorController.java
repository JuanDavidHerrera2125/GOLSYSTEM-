package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Jugador;
import JUANDEV.PRO.GOLSYSTEM.service.JugadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    // Inyectamos la Interfaz (JugadorService) en lugar de la Impl directamente
    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    // 1. OBTENER TODOS LOS JUGADORES
    @GetMapping
    public ResponseEntity<List<Jugador>> getAll() {
        List<Jugador> jugadores = jugadorService.findAll();
        return jugadores.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(jugadores);
    }

    // 2. OBTENER JUGADOR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Jugador> getById(@PathVariable Long id) {
        return jugadorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. BUSCAR POR DOCUMENTO (Crucial para validaciones en el Front)
    @GetMapping("/documento/{doc}")
    public ResponseEntity<Jugador> getByDocumento(@PathVariable String doc) {
        return jugadorService.findByDocumento(doc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. VER NÓMINA DE UN EQUIPO ESPECÍFICO
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<Jugador>> getByEquipo(@PathVariable Long equipoId) {
        List<Jugador> nomina = jugadorService.findByEquipo(equipoId);
        return nomina.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(nomina);
    }

    // 5. CREAR JUGADOR
    @PostMapping
    public ResponseEntity<Jugador> create(@RequestBody Jugador jugador) {
        try {
            Jugador nuevoJugador = jugadorService.save(jugador);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoJugador);
        } catch (Exception e) {
            // Maneja errores como documentos duplicados si los programaste en el Service
            return ResponseEntity.badRequest().build();
        }
    }

    // 6. ACTUALIZAR JUGADOR
    @PutMapping("/{id}")
    public ResponseEntity<Jugador> update(@PathVariable Long id, @RequestBody Jugador jugador) {
        try {
            return ResponseEntity.ok(jugadorService.update(jugador, id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 7. ELIMINAR JUGADOR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            jugadorService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}