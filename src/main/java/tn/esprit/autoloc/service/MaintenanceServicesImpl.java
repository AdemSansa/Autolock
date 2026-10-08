package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.repository.MaintenanceRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class MaintenanceServicesImpl implements IMaintenanceServices {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    public Maintenance create(Maintenance maintenance) {
        return maintenanceRepository.save(maintenance);
    }

    @Override
    @Transactional(readOnly = true)
    public Maintenance findById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance introuvable avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> findAll() {
        return maintenanceRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        maintenanceRepository.deleteById(id);
    }

    @Override
    public Maintenance update(Maintenance maintenance) {
        return maintenanceRepository.save(maintenance);
    }
}