package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.repository.EquipementRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class EquipementServicesImpl implements IEquipementServices {

    private final EquipementRepository equipementRepository;

    @Override
    public Equipement create(Equipement equipement) {
        return equipementRepository.save(equipement);
    }

    @Override
    @Transactional(readOnly = true)
    public Equipement findById(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Équipement introuvable avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipement> findAll() {
        return equipementRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        equipementRepository.deleteById(id);
    }

    @Override
    public Equipement update(Equipement equipement) {
        return equipementRepository.save(equipement);
    }
}