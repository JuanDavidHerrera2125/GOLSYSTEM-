package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.PosicionEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PosicionEquipoRepository extends JpaRepository<PosicionEquipo , Long> {

    // Busca la posición de un equipo específico en una tabla específica (Torneo/Grupo)
    Optional<PosicionEquipo> findByTablaIdAndEquipoId(Long tablaId, Long equipoId);

}
