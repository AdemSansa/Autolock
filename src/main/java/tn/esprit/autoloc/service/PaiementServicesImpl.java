package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.repository.PaiementRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class PaiementServicesImpl implements IPaiementServices {

    private final PaiementRepository paiementRepository;

    @Override
    public Paiement create(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    @Override
    @Transactional(readOnly = true)
    public Paiement findById(Long id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        paiementRepository.deleteById(id);
    }

    @Override
    public Paiement update(Paiement paiement) {
        return paiementRepository.save(paiement);
    }
}