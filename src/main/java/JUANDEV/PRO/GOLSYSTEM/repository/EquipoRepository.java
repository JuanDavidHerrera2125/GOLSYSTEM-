package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    // Opción A: Por convención de nombres (Torneos_Id entra en la lista ManyToMany)
    Optional<Equipo> findByNombreAndTorneos_Id(String nombre, Long torneoId);

    // Opción B: Por Query explícita (Más segura para evitar errores de convención)
    @Query("SELECT e FROM Equipo e JOIN e.torneos t WHERE e.nombre = :nombre AND t.id = :torneoId")
    Optional<Equipo> buscarPorNombreYTorneo(@Param("nombre") String nombre, @Param("torneoId") Long torneoId);
}