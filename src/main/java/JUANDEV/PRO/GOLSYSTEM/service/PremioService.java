package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Premio;

import java.util.List;
import java.util.Optional;

public interface PremioService {

    //Guardar nuevo premio
    Premio save (Premio premio);

    //Buscar premio por Id
    Optional<Premio> findById(Long id);

    //Buscar todos los premios
    List<Premio> findAll();

    //Modificar premio
    Premio update (Premio premio , Long id);

    //Eliminar premio
    void delete (Long id);

    //Contar premios
    long count();


}
