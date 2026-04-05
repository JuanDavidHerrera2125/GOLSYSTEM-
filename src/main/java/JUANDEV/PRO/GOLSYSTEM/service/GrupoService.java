package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Grupo;

import java.util.List;
import java.util.Optional;

public interface GrupoService {

    //Crear nuevo grupo
    Grupo save (Grupo grupo);

    //Listar todos los grupos
    List<Grupo> findAll();

    //Seleccionar grupo por ID
    Optional<Grupo> findById(Long id);

    //Eliminar grupo
    void deleteById (Long id);

    //Actualizar grupo
    Grupo update (Grupo grupo , Long id);

    //contar grupo
    long count();
}
