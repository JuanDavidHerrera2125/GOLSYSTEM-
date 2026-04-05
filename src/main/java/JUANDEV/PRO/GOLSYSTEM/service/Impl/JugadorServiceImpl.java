package JUANDEV.PRO.GOLSYSTEM.service.Impl;

import JUANDEV.PRO.GOLSYSTEM.model.Jugador;
import JUANDEV.PRO.GOLSYSTEM.repository.JugadorRepository;
import JUANDEV.PRO.GOLSYSTEM.service.JugadorService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JugadorServiceImpl implements JugadorService {

    @Autowired
    private JugadorRepository jugadorRepository;


    @Override
    public Jugador save(Jugador jugador) {
        return jugadorRepository.save(jugador);
    }

    @Override
    public Optional<Jugador> findById(Long id) {
        return jugadorRepository.findById(id);
    }

    @Override
    public List<Jugador> findAll() {
        return jugadorRepository.findAll();
    }

    @Override
    public Jugador update(Jugador jugador, Long id) {
        return jugadorRepository.findById(id)
                .map(existing->{
                    existing.setNombre(jugador.getNombre());
                    existing.setApellido(jugador.getApellido());
                    existing.setNumero(jugador.getNumero());
                    existing.setFechaNacimiento(jugador.getFechaNacimiento());
                    existing.setDocumento(jugador.getDocumento());
                    existing.setFoto(jugador.getFoto());
                    return jugadorRepository.save(existing);
                })
                .orElseThrow(()->new RuntimeException("Jugador no encontrado"));
    }

    @Override
    public void deleteById(Long id) {
        jugadorRepository.deleteById(id);

    }

    @Override
    public Optional<Jugador> findByNombre(String nombre) {
        return jugadorRepository.findByNombre(nombre);
    }

    @Override
    public Optional<Jugador> findByApellido(String apellido) {
        return jugadorRepository.findByApellido(apellido);
    }

    @Override
    public Optional<Jugador> findByNumero(Integer numero) {
        return jugadorRepository.findByNumero(numero);
    }
}
