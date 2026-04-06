package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Premio;
import JUANDEV.PRO.GOLSYSTEM.service.Impl.PremioServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/premios")
public class PremioController {

    private final PremioServiceImpl premioService;

    public PremioController(PremioServiceImpl premioService) {
        this.premioService = premioService;
    }

    @GetMapping
    public ResponseEntity<List<Premio>> getAll() {
        return ResponseEntity.ok(premioService.findAll());
    }

    @PostMapping
    public ResponseEntity<Premio> create(@RequestBody Premio premio) {
        return ResponseEntity.ok(premioService.save(premio));
    }
}