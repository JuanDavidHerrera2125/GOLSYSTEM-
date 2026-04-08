package JUANDEV.PRO.GOLSYSTEM.controller;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoTarjeta;
import JUANDEV.PRO.GOLSYSTEM.model.ResultadoPartido;
import JUANDEV.PRO.GOLSYSTEM.service.ResultadoPartidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partidos/{partidoId}/resultados")
public class ResultadoPartidoController {

    private final ResultadoPartidoService resultadoService;

    public ResultadoPartidoController(ResultadoPartidoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    @PostMapping
    public ResponseEntity<ResultadoPartido> registrarResultado(
            @PathVariable Long partidoId,
            @RequestBody ResultadoPartido resultado) {
        ResultadoPartido nuevoResultado = resultadoService.registrarResultado(partidoId, resultado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoResultado);
    }

    @PostMapping("/goles")
    public ResponseEntity<Void> registrarGol(
            @PathVariable Long partidoId,
            @RequestParam Long jugadorId,
            @RequestParam Integer minuto,
            @RequestParam(required = false, defaultValue = "false") Boolean esPenal,
            @RequestParam(required = false, defaultValue = "false") Boolean esAutogol) {

        resultadoService.registrarGol(partidoId, jugadorId, minuto, esPenal, esAutogol);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tarjetas")
    public ResponseEntity<Void> registrarTarjeta(
            @PathVariable Long partidoId,
            @RequestParam Long jugadorId,
            @RequestParam TipoTarjeta tipoTarjeta,
            @RequestParam Integer minuto) {

        resultadoService.registrarTarjeta(partidoId, jugadorId, tipoTarjeta, minuto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cerrar")
    public ResponseEntity<Void> cerrarPartido(@PathVariable Long partidoId) {
        resultadoService.cerrarPartido(partidoId);
        return ResponseEntity.ok().build();
    }
}