package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Fase;
import JUANDEV.PRO.GOLSYSTEM.service.ClasificacionService;
import JUANDEV.PRO.GOLSYSTEM.service.FaseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Crucial para conectar con el Frontend
public class FaseController {

    private final FaseService faseService;
    private final ClasificacionService clasificacionService;

    // ================= CRUD BÁSICO =================

    @GetMapping
    public ResponseEntity<List<Fase>> getAll() {
        List<Fase> fases = faseService.findAll();
        if (fases.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return faseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Fase> create(@RequestBody Fase fase) {
        return ResponseEntity.status(HttpStatus.CREATED).body(faseService.save(fase));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Fase fase) {
        try {
            return ResponseEntity.ok(faseService.update(fase, id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontró la fase para actualizar."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        faseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================= LÓGICA DE TORNEO (PRODUCCIÓN) =================

    /**
     * PROMOCIÓN DE FASE: El endpoint más importante.
     * Ejecuta la lógica de clasificación y genera la siguiente etapa.
     */
    @PostMapping("/{id}/promover")
    public ResponseEntity<?> promoverSiguienteFase(@PathVariable Long id) {
        try {
            clasificacionService.generarSiguienteFase(id);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Equipos promovidos y siguiente fase creada correctamente."
            ));
        } catch (IllegalStateException e) {
            // Error de negocio: Partidos pendientes, falta de equipos, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Fase no encontrada."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado en el servidor."));
        }
    }

    @PostMapping("/torneo/{torneoId}")
    public ResponseEntity<Fase> createInTorneo(@PathVariable Long torneoId, @RequestBody Fase fase) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(faseService.createFaseInTorneo(torneoId, fase));
    }

    @PostMapping("/{id}/equipos")
    public ResponseEntity<?> assignEquipos(@PathVariable Long id, @RequestBody List<Long> equiposIds) {
        if (equiposIds == null || equiposIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La lista de equipos no puede estar vacía."));
        }
        faseService.assignEquiposToFase(id, equiposIds);
        return ResponseEntity.ok(Map.of("message", "Equipos vinculados a la fase exitosamente."));
    }

    @PostMapping("/{id}/generar-grupos")
    public ResponseEntity<?> generateGrupos(@PathVariable Long id, @RequestParam int numGrupos) {
        if (numGrupos <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "El número de grupos debe ser mayor a cero."));
        }
        faseService.generateGruposInFase(id, numGrupos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Sorteo realizado y grupos generados."));
    }

    @PostMapping("/{id}/crear-tabla")
    public ResponseEntity<?> createTabla(@PathVariable Long id) {
        faseService.createTablaForFase(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Estructura de tabla de posiciones creada."));
    }
}