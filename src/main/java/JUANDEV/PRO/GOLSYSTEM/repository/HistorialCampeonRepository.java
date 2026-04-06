package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.HistorialCampeon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface HistorialCampeonRepository extends JpaRepository<HistorialCampeon, Long> {

    // 1. El método que te está pidiendo el Service para validar duplicados
    List<HistorialCampeon> findByTorneoId(Long torneoId);

    // 2. La consulta personalizada del ranking que corregimos hace un momento
    @Query("SELECT hc.equipo.nombre, COUNT(hc) FROM HistorialCampeon hc GROUP BY hc.equipo.nombre ORDER BY COUNT(hc) DESC")
    List<Object[]> rankingEquiposMasTitulos();
}