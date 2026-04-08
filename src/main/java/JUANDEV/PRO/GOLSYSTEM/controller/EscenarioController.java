package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Escenario;
import JUANDEV.PRO.GOLSYSTEM.service.EscenarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/escenarios")
public class EscenarioController {

    // 1. Cambiamos Repository por Service para usar tu lógica de negocio
    private final EscenarioService escenarioService;

    public EscenarioController(EscenarioService escenarioService) {
        this.escenarioService = escenarioService;
    }

    // 2. OBTENER TODOS
    @GetMapping
    public ResponseEntity<List<Escenario>> getAll() {
        List<Escenario> escenarios = escenarioService.findAll();
        return escenarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(escenarios);
    }

    // 3. OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Escenario> getById(@PathVariable Long id) {
        return escenarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. CREAR (Ahora usa el service.save)
    @PostMapping
    public ResponseEntity<Escenario> create(@RequestBody Escenario escenario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(escenarioService.save(escenario));
    }

    // 5. ACTUALIZAR (Llamando a tu lógica de update)
    @PutMapping("/{id}")
    public ResponseEntity<Escenario> update(@PathVariable Long id, @RequestBody Escenario escenario) {
        try {
            return ResponseEntity.ok(escenarioService.update(escenario, id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. ELIMINAR (Ejecuta la validación de partidos asociados)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            escenarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Devuelve el mensaje de error si tiene partidos asociados
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}