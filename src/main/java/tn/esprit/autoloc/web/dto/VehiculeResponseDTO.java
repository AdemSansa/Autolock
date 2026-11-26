package tn.esprit.autoloc.web.dto;

import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO détaillé : consultation d'un véhicule (GET /vehicules/{id}).
 */
public record VehiculeResponseDTO(
        Long id,
        String immatriculation,
        String marque,
        String modele,
        CategorieVehicule categorie,
        BigDecimal tarifJournalier,
        StatutVehicule statut,
        String agenceNom,
        List<String> equipements
) {
}
