package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.model.Suspension;
import JUANDEV.PRO.GOLSYSTEM.service.Impl.SuspensionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suspensiones")
public class SuspensionController {

    private final SuspensionServiceImpl suspensionService;

    public SuspensionController(SuspensionServiceImpl suspensionService) {
        this.suspensionService = suspensionService;
    }

    @GetMapping
    public ResponseEntity<List<Suspension>> getAll() {
        return ResponseEntity.ok(suspensionService.findAll());
    }

    @PostMapping
    public ResponseEntity<Suspension> create(@RequestBody Suspension suspension) {
        return ResponseEntity.ok(suspensionService.save(suspension));
    }
}