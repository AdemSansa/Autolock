package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.EquipementRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.web.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class VehiculeServicesImpl implements IVehiculeServices {

    private final VehiculeRepository vehiculeRepository;
    private final AgenceRepository agenceRepository;
    private final EquipementRepository equipementRepository;

    @Override
    public Vehicule create(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable avec l'id : " + id));
    }

    @Override
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        vehiculeRepository.deleteById(id);
    }

    @Override
    public Vehicule update(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    // ---------- Affectations ----------

    /**
     * Crée un nouveau véhicule et l'affecte directement à une agence existante.
     */
    @Override
    @Transactional
    public Vehicule addAndAssignToAgence(Vehicule vehicule, Long idAgence) {
        Agence agence = findAgence(idAgence);
        vehicule.setAgence(agence);
        return vehiculeRepository.save(vehicule);
    }

    /**
     * Affecte (ou réaffecte) un véhicule existant à une agence existante.
     */
    @Override
    @Transactional
    public Vehicule assignToAgence(Long idVehicule, Long idAgence) {
        Vehicule vehicule = findById(idVehicule);
        Agence agence = findAgence(idAgence);
        vehicule.setAgence(agence);
        return vehiculeRepository.save(vehicule);
    }

    /**
     * Ajoute un équipement existant à un véhicule existant (relation ManyToMany, côté propriétaire : Vehicule).
     */
    @Override
    @Transactional
    public Vehicule assignEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = findById(idVehicule);
        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement introuvable avec l'id : " + idEquipement));
        if (!vehicule.getEquipements().contains(equipement)) {
            vehicule.getEquipements().add(equipement);
        }
        return vehiculeRepository.save(vehicule);
    }

    /**
     * Retire tous les équipements d'un véhicule (vide la table de jointure vehicule_equipement pour ce véhicule).
     */
    @Override
    @Transactional
    public Vehicule unassignEquipement(Long idVehicule) {
        Vehicule vehicule = findById(idVehicule);
        vehicule.getEquipements().clear();
        return vehiculeRepository.save(vehicule);
    }

    /**
     * Crée un véhicule avec ses équipements et l'affecte à une agence existante.
     * Les équipements sans id sont créés ; ceux avec un id doivent déjà exister.
     */
    @Override
    @Transactional
    public Vehicule addVehiculeAndEquipementsAndAssignToAgence(Vehicule vehicule, Long idAgence) {
        Agence agence = findAgence(idAgence);
        List<Equipement> equipements = new ArrayList<>();
        if (vehicule.getEquipements() != null) {
            for (Equipement e : vehicule.getEquipements()) {
                if (e.getIdEquipement() == null) {
                    equipements.add(equipementRepository.save(e));
                } else {
                    equipements.add(equipementRepository.findById(e.getIdEquipement())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Équipement introuvable avec l'id : " + e.getIdEquipement())));
                }
            }
        }
        vehicule.setEquipements(equipements);
        vehicule.setAgence(agence);
        return vehiculeRepository.save(vehicule);
    }

    private Agence findAgence(Long idAgence) {
        return agenceRepository.findById(idAgence)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable avec l'id : " + idAgence));
    }
}
