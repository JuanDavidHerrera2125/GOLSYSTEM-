package JUANDEV.PRO.GOLSYSTEM.service;

import JUANDEV.PRO.GOLSYSTEM.model.Equipo;

import java.util.List;

public interface ClasificacionService {

        List<Equipo> getClasificadosByGrupo(Long grupoId, int cantidad);

        List<Equipo> getMejoresTerceros(Long faseId, int cantidad);

        void generarSiguienteFase(Long faseId);

}
