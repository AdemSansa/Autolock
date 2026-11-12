package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.MaintenanceRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.web.exception.ResourceNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class MaintenanceServicesImpl implements IMaintenanceServices {

    private final MaintenanceRepository maintenanceRepository;
    private final VehiculeRepository vehiculeRepository;

    @Override
    public Maintenance create(Maintenance maintenance) {
        return maintenanceRepository.save(maintenance);
    }

    @Override
    @Transactional(readOnly = true)
    public Maintenance findById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance introuvable avec l'id : " + id));
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

    // ---------- Affectations ----------

    /**
     * Crée une maintenance pour un véhicule existant.
     * Si la maintenance est en cours (pas de date de fin), le véhicule passe au statut MAINTENANCE.
     */
    @Override
    public Maintenance addAndAssignToVehicule(Maintenance maintenance, Long idVehicule) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable avec l'id : " + idVehicule));
        if (maintenance.getDateFin() != null && maintenance.getDateDebut() != null
                && maintenance.getDateFin().isBefore(maintenance.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
        }
        maintenance.setVehicule(vehicule);
        if (maintenance.getDateFin() == null) {
            vehicule.setStatut(StatutVehicule.MAINTENANCE);
        }
        return maintenanceRepository.save(maintenance);
    }
}
