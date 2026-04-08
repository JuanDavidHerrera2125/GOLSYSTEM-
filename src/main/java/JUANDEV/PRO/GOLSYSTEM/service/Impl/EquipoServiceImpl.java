package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.Equipo;
import JUANDEV.PRO.GOLSYSTEM.repository.EquipoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.EquipoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;

    public EquipoServiceImpl(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

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
        return equipoRepository.save(equipo);
    }

    // Eliminacion segura manejando la relacion bidireccional ManyToMany
    @Override
    public void deleteById(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el equipo con el ID: " + id));

        // Remueve el equipo de cada torneo asociado para evitar errores de integridad
        if (equipo.getTorneos() != null) {
            equipo.getTorneos().forEach(torneo -> torneo.getEquipos().remove(equipo));
        }

        equipoRepository.delete(equipo);
    }

    // Actualizacion parcial de campos de la entidad equipo
    @Override
    public Equipo update(Equipo equipo, Long id) {
        return equipoRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(equipo.getNombre());
                    existing.setLogo(equipo.getLogo());
                    existing.setCiudad(equipo.getCiudad());
                    existing.setTecnico(equipo.getTecnico());
                    existing.setEstrellas(equipo.getEstrellas());
                    return equipoRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con id: " + id));
    }

    @Override
    public long count() {
        return equipoRepository.count();
    }

    // Busqueda especifica usando la convencion de nombres de Spring Data JPA para tablas unidas
    @Override
    public Optional<Equipo> findByNombreAndTorneoId(String nombre, Long torneoId) {
        return equipoRepository.findByNombreAndTorneos_Id(nombre, torneoId);
    }
}