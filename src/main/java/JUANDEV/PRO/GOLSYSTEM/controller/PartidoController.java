package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.service.PartidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/partidos")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    @GetMapping
    public ResponseEntity<List<Partido>> getAll() {
        return ResponseEntity.ok(partidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partido> getById(@PathVariable Long id) {
        return partidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Partido> update(@PathVariable Long id, @RequestBody Partido partido) {
        return ResponseEntity.ok(partidoService.update(id, partido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partidoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}