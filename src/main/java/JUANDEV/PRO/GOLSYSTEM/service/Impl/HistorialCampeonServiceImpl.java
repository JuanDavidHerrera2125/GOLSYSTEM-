package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.Equipo;
import JUANDEV.PRO.GOLSYSTEM.model.HistorialCampeon;
import JUANDEV.PRO.GOLSYSTEM.model.Torneo;
import JUANDEV.PRO.GOLSYSTEM.repository.EquipoRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.HistorialCampeonRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.TorneoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.HistorialCampeonService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class HistorialCampeonServiceImpl implements HistorialCampeonService {

    private final HistorialCampeonRepository historialCampeonRepository;
    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;

    public HistorialCampeonServiceImpl(HistorialCampeonRepository historialCampeonRepository,
                                       TorneoRepository torneoRepository,
                                       EquipoRepository equipoRepository) {
        this.historialCampeonRepository = historialCampeonRepository;
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
    }

    // ================= LÓGICA =================

    @Override
    public HistorialCampeon registrarCampeon(Long torneoId, Long equipoId) {

        // 🔍 Buscar torneo
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con id: " + torneoId));

        // Buscar equipo
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con id: " + equipoId));

        // Validación: evitar duplicar campeón en mismo torneo
        List<HistorialCampeon> existentes = historialCampeonRepository.findByTorneoId(torneoId);
        if (!existentes.isEmpty()) {
            throw new RuntimeException("Este torneo ya tiene campeón registrado");
        }

        // Crear registro histórico
        HistorialCampeon historial = new HistorialCampeon();
        historial.setTorneo(torneo);
        historial.setEquipo(equipo);
        historial.setAnio(torneo.getAnio());

        // ⭐ Sumar estrella al equipo
        equipo.setEstrellas(equipo.getEstrellas() + 1);

        // 💾 Guardar
        return historialCampeonRepository.save(historial);
    }

    @Override
    public List<HistorialCampeon> findByTorneo(Long torneoId) {
        return historialCampeonRepository.findByTorneoId(torneoId);
    }

    @Override
    public List<Object[]> rankingEquiposMasTitulos() {
        return historialCampeonRepository.rankingEquiposMasTitulos();
    }
}