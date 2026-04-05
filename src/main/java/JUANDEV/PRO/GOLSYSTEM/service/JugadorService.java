package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Jugador;

import java.util.List;
import java.util.Optional;

public interface JugadorService {

    //Guardar nuevo jugador
    Jugador save (Jugador jugador);

    //Buscar jugador por ID
    Optional<Jugador> findById(Long id);

    //Listar todos los jugadores
    List<Jugador> findAll();

    //Actualizar datos del jugador
    Jugador update (Jugador jugador, Long id);

    //Eliminar jugador
    void deleteById(Long id);

    //Buscar Jugador por nombre
    Optional<Jugador> findByNombre(String nombre);

    //Buscar Jugador por apellido
    Optional<Jugador> findByApellido(String apellido);

    //Buscar Jugador por numero de camiseta
    Optional<Jugador> findByNumero(Integer numero);

}
