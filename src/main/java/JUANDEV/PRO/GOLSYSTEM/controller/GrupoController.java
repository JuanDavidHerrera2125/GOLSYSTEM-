package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Grupo;
import JUANDEV.PRO.GOLSYSTEM.service.Impl.GrupoServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grupos")
public class GrupoController {

    private final GrupoServiceImpl grupoService;

    public GrupoController(GrupoServiceImpl grupoService) {
        this.grupoService = grupoService;
    }

    @GetMapping
    public ResponseEntity<List<Grupo>> getAll() {
        return ResponseEntity.ok(grupoService.findAll());
    }

    @PostMapping
    public ResponseEntity<Grupo> create(@RequestBody Grupo grupo) {
        return ResponseEntity.ok(grupoService.save(grupo));
    }
}