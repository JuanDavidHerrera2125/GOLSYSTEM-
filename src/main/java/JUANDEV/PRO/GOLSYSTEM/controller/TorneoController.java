package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.dto.EquipoDTO;
import JUANDEV.PRO.GOLSYSTEM.dto.TablaPosicionDTO;
import JUANDEV.PRO.GOLSYSTEM.model.Torneo;
import JUANDEV.PRO.GOLSYSTEM.model.TablaPosicion; // Asumo que este es tu modelo
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

    // 1. OBTENER TODOS LOS TORNEOS
    @GetMapping
    public ResponseEntity<List<Torneo>> getAll() {
        List<Torneo> torneos = torneoService.findAll();
        if (torneos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(torneos);
    }

    // 2. OBTENER UN TORNEO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Torneo> getById(@PathVariable Long id) {
        return torneoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CREAR UN TORNEO
    @PostMapping
    public ResponseEntity<Torneo> create(@RequestBody Torneo torneo) {
        Torneo nuevoTorneo = torneoService.createTorneo(torneo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTorneo);
    }

    // 4. ACTUALIZAR UN TORNEO
    @PutMapping("/{id}")
    public ResponseEntity<Torneo> update(@PathVariable Long id, @RequestBody Torneo torneo) {
        try {
            return ResponseEntity.ok(torneoService.update(id, torneo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. ELIMINAR UN TORNEO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        torneoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================= FLUJO DEL TORNEO =================

    // 6. GENERAR FIXTURE (CALENDARIO)
    @PostMapping("/{id}/generar")
    public ResponseEntity<Void> generarFixture(@PathVariable Long id) {
        torneoService.generateTorneo(id);
        return ResponseEntity.ok().build();
    }

    // 7. INICIAR EL TORNEO
    @PostMapping("/{id}/iniciar")
    public ResponseEntity<Void> iniciarTorneo(@PathVariable Long id) {
        torneoService.startTorneo(id);
        return ResponseEntity.ok().build();
    }

    // 8. FINALIZAR EL TORNEO
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Void> finalizarTorneo(@PathVariable Long id) {
        torneoService.finishTorneo(id);
        return ResponseEntity.ok().build();
    }

    // ================= CONSULTA DE TABLA DINÁMICA =================

    // 9. Obtiene la tabla filtrada por jornada o la acumulada completa
    @GetMapping("/{id}/tabla")
    public ResponseEntity<List<TablaPosicionDTO>> getTablaPosiciones(
            @PathVariable Long id,
            @RequestParam(required = false) Integer jornada) {

        List<TablaPosicionDTO> tabla;

        if (jornada != null) {
            // Te devuelve la tabla calculada hasta la jornada que le pidas (ej: fecha 2)
            tabla = torneoService.calcularTablaHastaJornada(id, jornada);
        } else {
            // Te devuelve la tabla acumulada final (con todos los partidos jugados)
            tabla = torneoService.calcularTablaAcumulada(id);
        }

        if (tabla.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tabla);
    }

    //10. Obteger todos los equipos que participan en el torneo

    @GetMapping("/{id}/equipos")
    public ResponseEntity<?> getEquiposPorTorneo(@PathVariable Long id) {
        // 1. Buscas el torneo
        Torneo torneo = torneoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        // 2. Extraemos solo la información básica que queremos mostrar
        List<Map<String, Object>> equiposSimplificados = torneo.getEquipos().stream()
                .map(equipo -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", equipo.getId());
                    map.put("nombre", equipo.getNombre());
                    map.put("estrellas", equipo.getEstrellas());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(equiposSimplificados);
    }
}