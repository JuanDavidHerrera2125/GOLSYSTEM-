package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Escenario;
import JUANDEV.PRO.GOLSYSTEM.repository.EscenarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/escenarios")
public class EscenarioController {

    private final EscenarioRepository escenarioRepository;

    public EscenarioController(EscenarioRepository escenarioRepository) {
        this.escenarioRepository = escenarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Escenario>> getAll() {
        return ResponseEntity.ok(escenarioRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Escenario> create(@RequestBody Escenario escenario) {
        return ResponseEntity.ok(escenarioRepository.save(escenario));
    }
}