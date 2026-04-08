package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.enums.TipoTarjeta;
import JUANDEV.PRO.GOLSYSTEM.model.ResultadoPartido;

public interface ResultadoPartidoService {

    // Registrar resultado completo del partido
    ResultadoPartido registrarResultado(Long partidoId, ResultadoPartido resultado);

    // Registrar gol
    void registrarGol(Long partidoId, Long jugadorId, Integer minuto, Boolean esPenal, Boolean esAutogol);

    // Registrar tarjeta
    void registrarTarjeta(Long partidoId, Long jugadorId, TipoTarjeta tipoTarjeta, Integer minuto);

    // Cerrar partido (FINALIZADO + actualizar tabla)
    void cerrarPartido(Long partidoId);
}