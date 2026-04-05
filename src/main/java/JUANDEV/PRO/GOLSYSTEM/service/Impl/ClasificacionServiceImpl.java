package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.*;
import JUANDEV.PRO.GOLSYSTEM.repository.FaseRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.GrupoRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.TablaPosicionRepository;
import JUANDEV.PRO.GOLSYSTEM.service.ClasificacionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class ClasificacionServiceImpl implements ClasificacionService {

    private final GrupoRepository grupoRepository;
    private final TablaPosicionRepository tablaRepository;
    private final FaseRepository faseRepository;

    public ClasificacionServiceImpl(GrupoRepository grupoRepository,
                                    TablaPosicionRepository tablaRepository,
                                    FaseRepository faseRepository) {
        this.grupoRepository = grupoRepository;
        this.tablaRepository = tablaRepository;
        this.faseRepository = faseRepository;
    }

    // ================= CLASIFICADOS POR GRUPO =================

    @Override
    public List<Equipo> getClasificadosByGrupo(Long grupoId, int cantidad) {

        TablaPosicion tabla = tablaRepository.findByGrupoId(grupoId)
                .orElseThrow(() -> new RuntimeException("Tabla no encontrada para el grupo"));

        return tabla.getPosiciones().stream()
                .sorted(Comparator.comparing(PosicionEquipo::getPosicion))
                .limit(cantidad)
                .map(PosicionEquipo::getEquipo)
                .toList();
    }

    // ================= MEJORES TERCEROS =================

    @Override
    public List<Equipo> getMejoresTerceros(Long faseId, int cantidad) {

        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada"));

        List<PosicionEquipo> terceros = new ArrayList<>();

        for (Grupo grupo : fase.getGrupos()) {

            TablaPosicion tabla = tablaRepository.findByGrupoId(grupo.getId())
                    .orElseThrow(() -> new RuntimeException("Tabla no encontrada"));

            tabla.getPosiciones().stream()
                    .filter(p -> p.getPosicion() == 3)
                    .findFirst()
                    .ifPresent(terceros::add);
        }

        //Ordenar terceros por criterios FIFA básicos
        terceros.sort(
                Comparator.comparing(PosicionEquipo::getPuntos).reversed()
                        .thenComparing(PosicionEquipo::getDg).reversed()
                        .thenComparing(PosicionEquipo::getGf).reversed()
        );

        return terceros.stream()
                .limit(cantidad)
                .map(PosicionEquipo::getEquipo)
                .toList();
    }

    // ================= GENERAR SIGUIENTE FASE =================

    @Override
    public void generarSiguienteFase(Long faseId) {

        Fase faseActual = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada"));

        // Obtener clasificados (top 2 por grupo)
        List<Equipo> clasificados = new ArrayList<>();

        for (Grupo grupo : faseActual.getGrupos()) {
            clasificados.addAll(getClasificadosByGrupo(grupo.getId(), 2));
        }

        // Validación mínima
        if (clasificados.isEmpty()) {
            throw new RuntimeException("No hay equipos clasificados");
        }

        // Crear nueva fase
        Fase nuevaFase = new Fase();
        nuevaFase.setNombre("Eliminatoria");
        nuevaFase.setOrden(faseActual.getOrden() + 1);
        nuevaFase.setTipoFase(faseActual.getTipoFase()); // puedes cambiar a ELIMINACION

        faseActual.getTorneo().addFase(nuevaFase);

        // Aquí luego conectarás con PartidoService para crear llaves
        // Ejemplo futuro:
        // generarCruces(clasificados);

        faseRepository.save(nuevaFase);
    }
}