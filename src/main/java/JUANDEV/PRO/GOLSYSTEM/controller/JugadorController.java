package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Jugador;
import JUANDEV.PRO.GOLSYSTEM.service.Impl.JugadorServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jugadores")
public class JugadorController {

    private final JugadorServiceImpl jugadorService;

    public JugadorController(JugadorServiceImpl jugadorService) {
        this.jugadorService = jugadorService;
    }

    @GetMapping
    public ResponseEntity<List<Jugador>> getAll() {
        return ResponseEntity.ok(jugadorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jugador> getById(@PathVariable Long id) {
        return jugadorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Jugador> create(@RequestBody Jugador jugador) {
        return ResponseEntity.ok(jugadorService.save(jugador));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jugadorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}