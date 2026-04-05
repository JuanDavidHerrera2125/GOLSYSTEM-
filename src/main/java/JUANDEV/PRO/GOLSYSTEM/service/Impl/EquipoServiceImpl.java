package JUANDEV.PRO.GOLSYSTEM.service.Impl;
import JUANDEV.PRO.GOLSYSTEM.model.Equipo;
import JUANDEV.PRO.GOLSYSTEM.model.Torneo;
import JUANDEV.PRO.GOLSYSTEM.repository.EquipoRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.TorneoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.EquipoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipoServiceImpl implements EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private TorneoRepository torneoRepository;


    @Override
    public List<Equipo> findAll() {

        return equipoRepository.findAll();
    }

    @Override
    public Optional<Equipo> findById(Long id) {
        return equipoRepository.findById(id);
    }

    @Override
    public Equipo save(Equipo equipo) {

        // Se asegura que el equipo se agregue correctamente al torneo
        Torneo torneo = equipo.getTorneo();
        if(torneo != null){
            torneo.getEquipos().add(equipo);
        }
        return equipoRepository.save(equipo);
    }

    @Override
    public void deleteById(Long id) {
        // 1. Buscamos el equipo o lanzamos error
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el equipo con el ID: " + id));

        // 2. Antes de eliminar, removerlo de la colección del torneo
        Torneo torneo = equipo.getTorneo();
        if (torneo != null) {
            torneo.getEquipos().remove(equipo);
        }

        // 3. Eliminamos el equipo físicamente
        equipoRepository.delete(equipo);
    }

    //Actualizar equipo existente
    @Override
    public Equipo update(Equipo equipo, Long id) {
        return equipoRepository.findById(id)
                .map(existing ->{

                    existing.setNombre(equipo.getNombre());
                    existing.setLogo(equipo.getLogo());
                    existing.setCiudad(equipo.getCiudad());
                    existing.setTecnico(equipo.getTecnico());
                    existing.setEstrellas(equipo.getEstrellas());
                    return equipoRepository.save(existing);

                })
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
    }

    //Contar equipos
    @Override
    public long count() {
        return equipoRepository.count();
    }

    @Override
    public Optional<Equipo> findByNombreAndTorneoId(String nombre, Long torneoId) {
        return equipoRepository.findByNombreAndTorneoId(nombre , torneoId);
    }
}
