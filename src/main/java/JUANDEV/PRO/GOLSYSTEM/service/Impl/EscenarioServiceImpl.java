package JUANDEV.PRO.GOLSYSTEM.service.Impl;


import JUANDEV.PRO.GOLSYSTEM.model.Escenario;
import JUANDEV.PRO.GOLSYSTEM.model.Partido;
import JUANDEV.PRO.GOLSYSTEM.repository.EscenarioRepository;
import JUANDEV.PRO.GOLSYSTEM.service.EscenarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EscenarioServiceImpl implements EscenarioService {

    @Autowired
    private EscenarioRepository escenarioRepository;

    //Guardar escenario
    @Override
    public Escenario save(Escenario escenario) {
        return escenarioRepository.save(escenario);
    }

    //Obtener todos los escenarios
    @Override
    public List<Escenario> findAll() {
        return escenarioRepository.findAll();
    }

    //Buscar escenario por Id
    @Override
    public Optional<Escenario> findById(Long id) {
        return escenarioRepository.findById(id);
    }

    //Buscar escenario por nombre
    @Override
    public Optional<Escenario> findByNombre(String nombre){
        return escenarioRepository.findByNombre(nombre);
    }


    //Eliminar escenario
    @Override
    public void deleteById(Long id) {
        // 1. Buscamos el escenario o lanzamos error
        Escenario escenario = escenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el escenario con el ID: " + id));

        // 2. Verificamos si tiene partidos asociados
        List<Partido> partidos = escenario.getPartidos();
        if (partidos != null && !partidos.isEmpty()) {
            throw new RuntimeException("No se puede eliminar el escenario porque tiene partidos asociados.");
        }

        // 3. Si no tiene partidos, lo eliminamos con seguridad
        escenarioRepository.delete(escenario);
    }

    //Actualizar escenario
    @Override
    public Escenario update(Escenario escenario, Long id) {
        return escenarioRepository.findById(id)
                .map(existing ->{

                    existing.setNombre(escenario.getNombre());
                    existing.setDireccion(escenario.getDireccion());
                    return escenarioRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Escenario no encontrado"));

    }
}
