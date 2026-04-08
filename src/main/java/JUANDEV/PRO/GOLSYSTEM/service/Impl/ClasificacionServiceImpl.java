package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.enums.EstadoPartido;
import JUANDEV.PRO.GOLSYSTEM.enums.TipoFase;
import JUANDEV.PRO.GOLSYSTEM.model.*;
import JUANDEV.PRO.GOLSYSTEM.repository.FaseRepository;
import JUANDEV.PRO.GOLSYSTEM.repository.TablaPosicionRepository;
import JUANDEV.PRO.GOLSYSTEM.service.ClasificacionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class ClasificacionServiceImpl implements ClasificacionService {

    private final TablaPosicionRepository tablaRepository;
    private final FaseRepository faseRepository;

    public ClasificacionServiceImpl(TablaPosicionRepository tablaRepository,
                                    FaseRepository faseRepository) {
        this.tablaRepository = tablaRepository;
        this.faseRepository = faseRepository;
    }

    @Override
    public List<Equipo> getClasificadosByGrupo(Long grupoId, int cantidad) {
        TablaPosicion tabla = tablaRepository.findByGrupoId(grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Tabla no encontrada para el grupo: " + grupoId));

        return tabla.getPosiciones().stream()
                .sorted(Comparator.comparing(PosicionEquipo::getPosicion))
                .limit(cantidad)
                .map(PosicionEquipo::getEquipo)
                .toList();
    }

    @Override
    public List<Equipo> getMejoresTerceros(Long faseId, int cantidad) {
        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new EntityNotFoundException("Fase no encontrada con ID: " + faseId));

        List<PosicionEquipo> terceros = new ArrayList<>();
        for (Grupo grupo : fase.getGrupos()) {
            tablaRepository.findByGrupoId(grupo.getId()).ifPresent(tabla -> {
                // Buscamos al que quedó en 3ra posición en cada tabla
                tabla.getPosiciones().stream()
                        .filter(p -> p.getPosicion() == 3)
                        .findFirst()
                        .ifPresent(terceros::add);
            });
        }

        // Ordenamos los terceros bajo criterios de desempate (Puntos > GD > GF)
        terceros.sort(Comparator.comparing(PosicionEquipo::getPuntos).reversed()
                .thenComparing(PosicionEquipo::getDg).reversed()
                .thenComparing(PosicionEquipo::getGf).reversed());

        return terceros.stream()
                .limit(cantidad)
                .map(PosicionEquipo::getEquipo)
                .toList();
    }

    @Override
    public void generarSiguienteFase(Long faseId) {
        // 1. Obtener la fase y validar existencia
        Fase faseActual = faseRepository.findById(faseId)
                .orElseThrow(() -> new EntityNotFoundException("Fase no encontrada con ID: " + faseId));

        // 2. VALIDACIÓN DE PRODUCCIÓN: ¿Están todos los partidos jugados?
        boolean hayPartidosPendientes = faseActual.getPartidos().stream()
                .anyMatch(p -> p.getEstado() != EstadoPartido.FINALIZADO);

        if (hayPartidosPendientes) {
            throw new IllegalStateException("Error: No se puede avanzar de fase porque aún hay partidos pendientes de finalizar.");
        }

        // 3. EVITAR DUPLICADOS (Idempotencia)
        boolean yaExisteSiguiente = faseActual.getTorneo().getFases().stream()
                .anyMatch(f -> f.getOrden() == (faseActual.getOrden() + 1));

        if (yaExisteSiguiente) {
            throw new IllegalStateException("La siguiente fase ya ha sido generada para este torneo.");
        }

        // 4. Lógica de clasificación según reglas
        ReglaCompeticion reglas = faseActual.getRegla();
        int cantClasificados = (reglas != null) ? reglas.getClasificadosPorGrupo() : 2;

        List<Equipo> clasificados = new ArrayList<>();

        // Clasificados directos por grupo
        for (Grupo grupo : faseActual.getGrupos()) {
            clasificados.addAll(getClasificadosByGrupo(grupo.getId(), cantClasificados));
        }

        // Mejores terceros si aplica
        if (reglas != null && reglas.getCantidadMejoresTerceros() > 0) {
            clasificados.addAll(getMejoresTerceros(faseId, reglas.getCantidadMejoresTerceros()));
        }

        // 5. VALIDACIÓN DE INTEGRIDAD: ¿El número de equipos permite llaves?
        if (clasificados.isEmpty() || clasificados.size() % 2 != 0) {
            throw new IllegalStateException("Integridad fallida: El número de clasificados (" + clasificados.size() + ") no es par. Ajuste las reglas del torneo.");
        }

        // 6. CREACIÓN DE LA NUEVA FASE
        Fase proximaFase = new Fase();
        proximaFase.setNombre("FASE ELIMINATORIA - " + (faseActual.getOrden() + 1));
        proximaFase.setOrden(faseActual.getOrden() + 1);
        proximaFase.setTipoFase(TipoFase.ELIMINACION);
        proximaFase.setActiva(false); // Inicia inactiva hasta que se definan cruces

        // Configuración de reglas automáticas para eliminación directa
        ReglaCompeticion nuevaRegla = new ReglaCompeticion();
        nuevaRegla.setEsMuerteSubita(true);
        nuevaRegla.setPermiteEmpate(false); // Fuerza penales/alargue
        nuevaRegla.setClasificadosPorGrupo(0); // No aplica en eliminación
        proximaFase.setRegla(nuevaRegla);

        // 7. VINCULACIÓN Y GUARDADO
        faseActual.getTorneo().addFase(proximaFase);
        faseRepository.save(proximaFase);

        // LOG de éxito en consola
        System.out.println("Fase generada exitosamente. Equipos promovidos: " + clasificados.size());
    }
}