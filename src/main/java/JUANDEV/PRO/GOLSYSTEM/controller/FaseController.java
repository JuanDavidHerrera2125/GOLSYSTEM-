package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Fase;
import JUANDEV.PRO.GOLSYSTEM.repository.FaseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fases")
public class FaseController {

    private final FaseRepository faseRepository;

    public FaseController(FaseRepository faseRepository) {
        this.faseRepository = faseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Fase>> getAll() {
        return ResponseEntity.ok(faseRepository.findAll());
    }
}