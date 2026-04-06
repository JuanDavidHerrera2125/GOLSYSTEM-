package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.dto.CrearEquipoDTO;
import JUANDEV.PRO.GOLSYSTEM.model.Equipo;
import JUANDEV.PRO.GOLSYSTEM.repository.EquipoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipos")
public class EquipoController {

    private final EquipoRepository equipoRepository;

    public EquipoController(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    // 1. OBTENER TODOS LOS EQUIPOS
    @GetMapping
    public ResponseEntity<List<Equipo>> getAll() {
        return ResponseEntity.ok(equipoRepository.findAll());
    }

    // 2. OBTENER UN EQUIPO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Equipo> getById(@PathVariable Long id) {
        return equipoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CREAR UN EQUIPO
    @PostMapping
    public ResponseEntity<Equipo> create(@RequestBody Equipo equipo) {
        return ResponseEntity.ok(equipoRepository.save(equipo));
    }

    // 4. ACTUALIZAR UN EQUIPO (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Equipo> update(@PathVariable Long id, @RequestBody CrearEquipoDTO dto) {
        return equipoRepository.findById(id)
                .map(equipoExistente -> {
                    // Mapeamos los datos del DTO a la entidad existente
                    equipoExistente.setNombre(dto.getNombre());
                    equipoExistente.setLogo(dto.getLogo());
                    equipoExistente.setCiudad(dto.getCiudad());
                    equipoExistente.setTecnico(dto.getTecnico());
                    // Guardamos los cambios
                    Equipo equipoActualizado = equipoRepository.save(equipoExistente);
                    return ResponseEntity.ok(equipoActualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. ELIMINAR UN EQUIPO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!equipoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        equipoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}