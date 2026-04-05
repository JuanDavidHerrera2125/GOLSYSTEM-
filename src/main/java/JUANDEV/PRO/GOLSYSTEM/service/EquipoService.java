package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Equipo;

import java.util.List;
import java.util.Optional;

public interface EquipoService {

    //Obtener todos los equipos registrados
    List<Equipo> findAll();


    //Buscar un equipo por su ID
    Optional<Equipo> findById(Long id);

    //Guardar un equipo
    Equipo save (Equipo equipo);

    //Eliminar un equipo
    void deleteById (Long id);

    //Actualizar Equipo
    Equipo update (Equipo equipo , Long id);

    //Contar Total de equipos registrados
    long count();

    Optional<Equipo> findByNombreAndTorneoId(String nombre , Long torneoId);


}
