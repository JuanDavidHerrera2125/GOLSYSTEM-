package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.EventoGol;
import JUANDEV.PRO.GOLSYSTEM.service.Impl.EventoGolServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goles")
public class EventoGolController {

    private final EventoGolServiceImpl eventoGolService;

    public EventoGolController(EventoGolServiceImpl eventoGolService) {
        this.eventoGolService = eventoGolService;
    }

    @GetMapping
    public ResponseEntity<List<EventoGol>> getAll() {
        return ResponseEntity.ok(eventoGolService.findAll());
    }

    @PostMapping
    public ResponseEntity<EventoGol> create(@RequestBody EventoGol gol) {
        return ResponseEntity.ok(eventoGolService.save(gol));
    }
}