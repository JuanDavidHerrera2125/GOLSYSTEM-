package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.Grupo;
import JUANDEV.PRO.GOLSYSTEM.repository.GrupoRepository;
import JUANDEV.PRO.GOLSYSTEM.service.GrupoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GrupoServiceImpl implements GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Override
    public Grupo save(Grupo grupo) {
        return grupoRepository.save(grupo);
    }

    @Override
    public List<Grupo> findAll() {
        return grupoRepository.findAll();
    }

    @Override
    public Optional<Grupo> findById(Long id) {
        return grupoRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        grupoRepository.deleteById(id);

    }

    @Override
    public Grupo update(Grupo grupo, Long id) {
        return grupoRepository.findById(id)
                .map(existing ->{
                    existing.setNombre(grupo.getNombre());
                    return grupoRepository.save(existing);
                })
                .orElseThrow(()->new RuntimeException("Grupo no encontrado"));
    }

    @Override
    public long count() {
        return grupoRepository.count();
    }
}
