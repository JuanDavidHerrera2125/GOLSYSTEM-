package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Escenario;

import java.util.List;
import java.util.Optional;

public interface EscenarioService{


    //Guardar escenario
    Escenario save (Escenario escenario);

    //Obtener todos los escenarios
    List<Escenario> findAll();

    //Buscar un escenario por Id
    Optional<Escenario> findById (Long id);

    //Eliminar Escenario
    void deleteById (Long id);

    //Actualizar escenario
    Escenario update (Escenario escenario, Long id);

    //Buscar escenario por nombre
    Optional<Escenario> findByNombre (String nombre);

}
