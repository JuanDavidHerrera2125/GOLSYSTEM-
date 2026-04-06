package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.TablaPosicion;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tablas")
public class TablaPosicionController {

    private final TablaPosicionService tablaPosicionService;

    public TablaPosicionController(TablaPosicionService tablaPosicionService) {
        this.tablaPosicionService = tablaPosicionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TablaPosicion> getById(@PathVariable Long id) {
        return tablaPosicionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/recalcular")
    public ResponseEntity<Void> recalcularTabla(@PathVariable Long id) {
        tablaPosicionService.recalculateTabla(id);
        return ResponseEntity.ok().build();
    }
}