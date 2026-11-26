package tn.esprit.autoloc.web.dto.vehicule;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversion manuelle Vehicule <-> DTO.
 */
@Component
public class VehiculeMapper {

    /**
     * DTO -> nouvelle entité. L'agence n'est pas résolue ici (le service la charge à partir de son id).
     * Les équipements sont des références : id seul = existant, libellé seul = à créer.
     */
    public Vehicule toEntity(VehiculeRequest request) {
        Vehicule vehicule = new Vehicule();
        copyFields(request, vehicule);
        vehicule.setEquipements(toEquipements(request));
        return vehicule;
    }

    /**
     * Applique le DTO sur une entité existante (PUT) : on garde l'id, l'agence, les équipements,
     * les réservations et les maintenances déjà en base.
     */
    public void updateEntity(VehiculeRequest request, Vehicule vehicule) {
        copyFields(request, vehicule);
    }

    public VehiculeResponse toResponse(Vehicule vehicule) {
        Agence agence = vehicule.getAgence();
        VehiculeResponse.AgenceSummary agenceSummary = agence == null ? null
                : new VehiculeResponse.AgenceSummary(agence.getIdAgence(), agence.getNom(), agence.getVille());

        List<VehiculeResponse.EquipementSummary> equipements = vehicule.getEquipements() == null ? List.of()
                : vehicule.getEquipements().stream()
                        .map(e -> new VehiculeResponse.EquipementSummary(e.getIdEquipement(), e.getLibelle()))
                        .toList();

        return new VehiculeResponse(
                vehicule.getIdVehicule(),
                vehicule.getImmatriculation(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getCategorie(),
                vehicule.getTarifJournalier(),
                vehicule.getStatut(),
                agenceSummary,
                equipements
        );
    }

    public List<VehiculeResponse> toResponseList(List<Vehicule> vehicules) {
        return vehicules.stream().map(this::toResponse).toList();
    }

    private void copyFields(VehiculeRequest request, Vehicule vehicule) {
        vehicule.setImmatriculation(request.immatriculation());
        vehicule.setMarque(request.marque());
        vehicule.setModele(request.modele());
        vehicule.setCategorie(request.categorie());
        vehicule.setTarifJournalier(request.tarifJournalier());
        vehicule.setStatut(request.statut());
    }

    private List<Equipement> toEquipements(VehiculeRequest request) {
        List<Equipement> equipements = new ArrayList<>();
        if (request.equipementIds() != null) {
            for (Long id : request.equipementIds()) {
                Equipement e = new Equipement();
                e.setIdEquipement(id);
                equipements.add(e);
            }
        }
        if (request.nouveauxEquipements() != null) {
            for (String libelle : request.nouveauxEquipements()) {
                Equipement e = new Equipement();
                e.setLibelle(libelle);
                equipements.add(e);
            }
        }
        return equipements;
    }
}
