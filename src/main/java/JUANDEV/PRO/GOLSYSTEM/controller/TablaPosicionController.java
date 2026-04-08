package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.TablaPosicion;
import JUANDEV.PRO.GOLSYSTEM.service.TablaPosicionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tablas")
public class TablaPosicionController {

    private final TablaPosicionService tablaPosicionService;

    public TablaPosicionController(TablaPosicionService tablaPosicionService) {
        this.tablaPosicionService = tablaPosicionService;
    }

    // --- LECTURA ---

    @GetMapping
    public ResponseEntity<List<TablaPosicion>> getAll() {
        List<TablaPosicion> tablas = tablaPosicionService.findAll();
        return tablas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tablas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TablaPosicion> getById(@PathVariable Long id) {
        return tablaPosicionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- ACCIONES DEL MOTOR ---

    /**
     * Recalcula desde cero toda la tabla basada en los partidos finalizados.
     * Útil para corregir inconsistencias.
     */
    @PostMapping("/{id}/recalcular")
    public ResponseEntity<String> recalcularTabla(@PathVariable Long id) {
        try {
            tablaPosicionService.recalculateTabla(id);
            return ResponseEntity.ok("Tabla recalculada y ordenada exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al recalcular: " + e.getMessage());
        }
    }

    /**
     * Permite disparar la actualización manual de un solo partido.
     * Útil si un webhook o proceso automático falla.
     */
    @PatchMapping("/actualizar-por-partido/{partidoId}")
    public ResponseEntity<Void> actualizarDesdePartido(@PathVariable Long partidoId) {
        tablaPosicionService.updateTablaFromPartido(partidoId);
        return ResponseEntity.accepted().build();
    }

    // --- MANTENIMIENTO ---

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tablaPosicionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}