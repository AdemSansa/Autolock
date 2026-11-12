package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Vehicule;

import java.util.List;

public interface IVehiculeServices {
    Vehicule  create (Vehicule vehicule);
    Vehicule findById(Long id);
    List <Vehicule> findAll();
    void deleteById(Long id);
    Vehicule update (Vehicule vehicule);

    // Affectations
    Vehicule addAndAssignToAgence(Vehicule vehicule, Long idAgence);
    Vehicule assignToAgence(Long idVehicule, Long idAgence);
    Vehicule assignEquipement(Long idVehicule, Long idEquipement);
    Vehicule unassignEquipement(Long idVehicule);
    Vehicule unassignEquipement(Long idVehicule, Long idEquipement);
    Vehicule addVehiculeAndEquipementsAndAssignToAgence(Vehicule vehicule, Long idAgence);
}
