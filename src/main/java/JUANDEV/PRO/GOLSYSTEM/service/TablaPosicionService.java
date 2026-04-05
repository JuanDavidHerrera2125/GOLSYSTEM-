package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.TablaPosicion;

import java.util.List;
import java.util.Optional;

public interface TablaPosicionService {

    // ================= CRUD =================

    TablaPosicion save(TablaPosicion tabla);

    Optional<TablaPosicion> findById(Long id);

    List<TablaPosicion> findAll();

    TablaPosicion update(Long id, TablaPosicion tabla);

    void deleteById(Long id);

    long count();

    // ================= LÓGICA =================

    // Actualizar tabla después de un partido
    void updateTablaFromPartido(Long partidoId);

    // Recalcular completamente una tabla (por seguridad)
    void recalculateTabla(Long tablaId);

    // Ordenar tabla según criterios (ranking)
    void ordenarTabla(Long tablaId);
}