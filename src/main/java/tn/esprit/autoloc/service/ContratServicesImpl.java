package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.repository.ContratRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ContratServicesImpl implements IContratServices {

    private final ContratRepository contratRepository;

    @Override
    public Contrat create(Contrat contrat) {
        return contratRepository.save(contrat);
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        contratRepository.deleteById(id);
    }

    @Override
    public Contrat update(Contrat contrat) {
        return contratRepository.save(contrat);
    }
}