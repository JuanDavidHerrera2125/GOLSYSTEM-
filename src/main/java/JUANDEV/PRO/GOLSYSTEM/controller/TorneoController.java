package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.dto.TablaPosicionDTO;
import JUANDEV.PRO.GOLSYSTEM.model.Torneo;
import JUANDEV.PRO.GOLSYSTEM.service.TorneoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/torneos")
public class TorneoController {

    private final TorneoService torneoService;

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    // ================= CRUD BÁSICO =================

    @GetMapping
    public ResponseEntity<List<Torneo>> getAll() {
        List<Torneo> torneos = torneoService.findAll();
        return torneos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(torneos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Torneo> getById(@PathVariable Long id) {
        return torneoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Torneo> create(@RequestBody Torneo torneo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(torneoService.createTorneo(torneo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Torneo> update(@PathVariable Long id, @RequestBody Torneo torneo) {
        try {
            return ResponseEntity.ok(torneoService.update(id, torneo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        torneoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================= GESTIÓN DE EQUIPOS (MANY-TO-MANY) =================

    // 🌟 INSCRIBIR UN EQUIPO EXISTENTE EN EL TORNEO
    @PostMapping("/{torneoId}/equipos/{equipoId}")
    public ResponseEntity<String> enrollTeam(@PathVariable Long torneoId, @PathVariable Long equipoId) {
        try {
            torneoService.enrollTeam(torneoId, equipoId);
            return ResponseEntity.ok("Equipo inscrito correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // LISTAR EQUIPOS DEL TORNEO (Simplificado para el Front)
    @GetMapping("/{id}/equipos")
    public ResponseEntity<List<Map<String, Object>>> getEquiposByTorneo(@PathVariable Long id) {
        return torneoService.findById(id)
                .map(torneo -> {
                    List<Map<String, Object>> res = torneo.getEquipos().stream().map(e -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", e.getId());
                        m.put("nombre", e.getNombre());
                        m.put("logo", e.getLogo());
                        return m;
                    }).collect(Collectors.toList());
                    return ResponseEntity.ok(res);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= FLUJO Y ESTADOS =================

    @PostMapping("/{id}/generate")
    public ResponseEntity<Void> generate(@PathVariable Long id) {
        torneoService.generateTorneo(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> start(@PathVariable Long id) {
        torneoService.startTorneo(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<Void> finish(@PathVariable Long id) {
        torneoService.finishTorneo(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archive(@PathVariable Long id) {
        torneoService.archiveTorneo(id);
        return ResponseEntity.ok().build();
    }

    // ================= TABLA DE POSICIONES =================

    @GetMapping("/{id}/tabla")
    public ResponseEntity<List<TablaPosicionDTO>> getTabla(@PathVariable Long id, @RequestParam(required = false) Integer jornada) {
        List<TablaPosicionDTO> tabla = (jornada != null)
                ? torneoService.calcularTablaHastaJornada(id, jornada)
                : torneoService.calcularTablaAcumulada(id);
        return tabla.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tabla);
    }
}