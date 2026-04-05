package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.HistorialCampeon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialCampeonRepository extends JpaRepository<HistorialCampeon, Long> {
    List<HistorialCampeon> findByTorneoId(Long torneoId);

    List<Object[]> rankingEquiposMasTitulos();
}
