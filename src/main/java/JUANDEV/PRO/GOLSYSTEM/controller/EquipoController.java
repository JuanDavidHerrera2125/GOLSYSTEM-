package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Equipo;
import JUANDEV.PRO.GOLSYSTEM.model.Jugador;
import JUANDEV.PRO.GOLSYSTEM.service.EquipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    // Obtener lista completa de equipos
    @GetMapping
    public ResponseEntity<List<Equipo>> getAll() {
        List<Equipo> equipos = equipoService.findAll();
        return equipos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(equipos);
    }

    // Obtener detalle de un equipo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Equipo> getById(@PathVariable Long id) {
        return equipoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar jugadores de un equipo con estructura simplificada para el front
    @GetMapping("/{id}/jugadores")
    public ResponseEntity<List<Map<String, Object>>> getJugadores(@PathVariable Long id) {
        return equipoService.findById(id)
                .map(equipo -> {
                    List<Map<String, Object>> jugadores = equipo.getJugadores().stream().map(j -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", j.getId());
                        data.put("nombreCompleto", j.getNombre() + " " + j.getApellido());
                        data.put("numero", j.getNumero());
                        data.put("posicion", j.getFoto()); // O el campo que uses para posición
                        return data;
                    }).collect(Collectors.toList());
                    return ResponseEntity.ok(jugadores);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Registrar nuevo equipo
    @PostMapping
    public ResponseEntity<Equipo> create(@RequestBody Equipo equipo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.save(equipo));
    }

    // Actualizar informacion de equipo existente
    @PutMapping("/{id}")
    public ResponseEntity<Equipo> update(@PathVariable Long id, @RequestBody Equipo equipo) {
        try {
            return ResponseEntity.ok(equipoService.update(equipo, id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar equipo y limpiar sus asociaciones
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            equipoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Conteo total de equipos registrados
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(equipoService.count());
    }

    // Busqueda de equipo por nombre filtrado por torneo
    @GetMapping("/buscar")
    public ResponseEntity<Equipo> findByNombreAndTorneo(
            @RequestParam String nombre,
            @RequestParam Long torneoId) {
        return equipoService.findByNombreAndTorneoId(nombre, torneoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}