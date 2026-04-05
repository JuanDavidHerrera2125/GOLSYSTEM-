package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.Premio;
import JUANDEV.PRO.GOLSYSTEM.repository.PremioRepository;
import JUANDEV.PRO.GOLSYSTEM.service.PremioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PremioServiceImpl implements PremioService {

    @Autowired
    private  PremioRepository premioRepository;


    @Override
    public Premio save(Premio premio) {
        return premioRepository.save(premio);
    }

    @Override
    public Optional<Premio> findById(Long id) {
        return premioRepository.findById(id);
    }

    @Override
    public List<Premio> findAll() {
        return premioRepository.findAll();
    }

    @Override
    public Premio update(Premio premio, Long id) {
        return premioRepository.findById(id)
                .map(existing->{
                    existing.setTipo(premio.getTipo());
                    return premioRepository.save(existing);
                })
                .orElseThrow(()->new RuntimeException("Premio no encontrado"))
                ;
    }

    @Override
    public void delete(Long id) {
        premioRepository.deleteById(id);

    }

    @Override
    public long count() {
        return premioRepository.count();
    }
}
