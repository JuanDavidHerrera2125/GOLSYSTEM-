package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartidoRepository extends JpaRepository<Partido, Long> {
    // Para ver partidos por estado (ej: todos los EN_JUEGO para la App)
    List<Partido> findByEstado(EstadoPartido estado);

    // Para obtener el calendario de un equipo específico
    List<Partido> findByEquipoLocalIdOrEquipoVisitanteId(Long localId, Long visitanteId);

    // Estas son las que te faltan y causan el error en el Service:
    List<Partido> findByGrupoIdAndEstado(Long grupoId, EstadoPartido estado);

    List<Partido> findByFaseIdAndGrupoIsNullAndEstado(Long faseId, EstadoPartido estado);

    List<Partido> findByFaseIdAndEstado(Long faseId, EstadoPartido estado);
}