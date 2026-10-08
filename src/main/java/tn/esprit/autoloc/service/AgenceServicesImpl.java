package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.repository.AgenceRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class AgenceServicesImpl implements IAgenceServices {

    private final AgenceRepository agenceRepository;

    @Override
    public Agence create(Agence agence) {
        return agenceRepository.save(agence);
    }

    @Override
    @Transactional(readOnly = true)
    public Agence findById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agence introuvable avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        agenceRepository.deleteById(id);
    }

    @Override
    public Agence update(Agence agence) {
        return agenceRepository.save(agence);
    }
}