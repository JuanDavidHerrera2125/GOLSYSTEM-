package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.EventoTarjeta;
import JUANDEV.PRO.GOLSYSTEM.service.Impl.EventoTarjetaServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tarjetas")
public class EventoTarjetaController {

    private final EventoTarjetaServiceImpl eventoTarjetaService;

    public EventoTarjetaController(EventoTarjetaServiceImpl eventoTarjetaService) {
        this.eventoTarjetaService = eventoTarjetaService;
    }

    @GetMapping
    public ResponseEntity<List<EventoTarjeta>> getAll() {
        return ResponseEntity.ok(eventoTarjetaService.findAll());
    }

    @PostMapping
    public ResponseEntity<EventoTarjeta> create(@RequestBody EventoTarjeta tarjeta) {
        return ResponseEntity.ok(eventoTarjetaService.save(tarjeta));
    }
}