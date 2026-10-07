package tn.esprit.autoloc.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Insère une agence et quelques véhicules de démonstration au démarrage, uniquement avec le profil « dev »
 * et seulement si la table est vide (pour ne pas violer l'unicité de l'immatriculation au redémarrage).
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DemoDataLoader implements CommandLineRunner {

    private final VehiculeRepository vehiculeRepository;
    private final AgenceRepository agenceRepository;

    @Override
    public void run(String... args) {
        if (vehiculeRepository.count() > 0) {
            return;
        }
        Agence agence = agenceRepository.save(agence("AutoLoc Tunis Centre", "Tunis",
                "10 Avenue Habib Bourguiba", "71 000 000"));
        List<Vehicule> vehicules = vehiculeRepository.saveAll(List.of(
                vehicule("123 TU 4567", "Renault", "Clio", CategorieVehicule.CITADINE,
                        new BigDecimal("80.00"), StatutVehicule.DISPONIBLE, agence),
                vehicule("234 TU 5678", "Peugeot", "508", CategorieVehicule.BERLINE,
                        new BigDecimal("150.00"), StatutVehicule.DISPONIBLE, agence),
                vehicule("345 TU 6789", "Kia", "Sportage", CategorieVehicule.SUV,
                        new BigDecimal("180.00"), StatutVehicule.MAINTENANCE, agence)
        ));
        log.info("{} véhicules de démonstration insérés", vehicules.size());
    }

    private Agence agence(String nom, String ville, String adresse, String telephone) {
        Agence agence = new Agence();
        agence.setNom(nom);
        agence.setVille(ville);
        agence.setAdresse(adresse);
        agence.setTelephone(telephone);
        return agence;
    }

    private Vehicule vehicule(String immatriculation, String marque, String modele,
                              CategorieVehicule categorie, BigDecimal tarif, StatutVehicule statut,
                              Agence agence) {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(immatriculation);
        vehicule.setMarque(marque);
        vehicule.setModele(modele);
        vehicule.setCategorie(categorie);
        vehicule.setTarifJournalier(tarif);
        vehicule.setStatut(statut);
        vehicule.setAgence(agence);
        return vehicule;
    }
}
