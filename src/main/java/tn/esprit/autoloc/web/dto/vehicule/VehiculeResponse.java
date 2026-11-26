package tn.esprit.autoloc.web.dto.vehicule;

import io.swagger.v3.oas.annotations.media.Schema;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

import java.math.BigDecimal;
import java.util.List;

/**
 * Représentation d'un véhicule renvoyée par l'API (pas de proxy Hibernate, pas de boucle JSON).
 */
@Schema(description = "Véhicule renvoyé par l'API")
public record VehiculeResponse(
        Long idVehicule,
        String immatriculation,
        String marque,
        String modele,
        CategorieVehicule categorie,
        BigDecimal tarifJournalier,
        StatutVehicule statut,
        AgenceSummary agence,
        List<EquipementSummary> equipements
) {

    @Schema(description = "Agence du véhicule (résumé)")
    public record AgenceSummary(Long idAgence, String nom, String ville) {
    }

    @Schema(description = "Équipement du véhicule (résumé)")
    public record EquipementSummary(Long idEquipement, String libelle) {
    }
}
