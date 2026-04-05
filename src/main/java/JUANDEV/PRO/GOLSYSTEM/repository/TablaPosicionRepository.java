package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.TablaPosicion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TablaPosicionRepository extends JpaRepository<TablaPosicion, Long> {

    //Buscar tabla por fase (liga)
    Optional<TablaPosicion> findByFaseIdAndGrupoIsNull(Long faseId);

    //Buscar tabla por grupo
    Optional<TablaPosicion> findByGrupoId(Long grupoId);


}
