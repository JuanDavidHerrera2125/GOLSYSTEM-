package JUANDEV.PRO.GOLSYSTEM.repository;

import JUANDEV.PRO.GOLSYSTEM.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    // Busqueda por documento para evitar duplicados
    Optional<Jugador> findByDocumento(String documento);

    // Busqueda de todos los jugadores de un equipo especifico
    List<Jugador> findByEquipoId(Long equipoId);

    Optional<Jugador> findByNombre(String nombre);
    
    Optional<Jugador> findByApellido(String apellido);

    Optional<Jugador> findByNumero(Integer numero);
}