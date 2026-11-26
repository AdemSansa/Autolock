package tn.esprit.autoloc.web.dto.vehicule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

import java.math.BigDecimal;
import java.util.List;

/**
 * Données reçues par l'API pour créer ou modifier un véhicule.
 * Aucune entité JPA n'est exposée : l'agence et les équipements sont référencés par leur id.
 */
@Schema(description = "Données d'entrée d'un véhicule")
public record VehiculeRequest(

        @Schema(example = "456 TU 7890")
        @NotBlank(message = "L'immatriculation est obligatoire")
        @Size(max = 20, message = "L'immatriculation ne doit pas dépasser 20 caractères")
        String immatriculation,

        @Schema(example = "Toyota")
        @NotBlank(message = "La marque est obligatoire")
        @Size(max = 50, message = "La marque ne doit pas dépasser 50 caractères")
        String marque,

        @Schema(example = "Yaris")
        @NotBlank(message = "Le modèle est obligatoire")
        @Size(max = 50, message = "Le modèle ne doit pas dépasser 50 caractères")
        String modele,

        @Schema(example = "CITADINE")
        @NotNull(message = "La catégorie est obligatoire")
        CategorieVehicule categorie,

        @Schema(example = "90.00")
        @NotNull(message = "Le tarif journalier est obligatoire")
        @PositiveOrZero(message = "Le tarif journalier ne peut pas être négatif")
        @Digits(integer = 8, fraction = 2, message = "Le tarif journalier doit avoir au plus 2 décimales")
        BigDecimal tarifJournalier,

        @Schema(example = "DISPONIBLE")
        @NotNull(message = "Le statut est obligatoire")
        StatutVehicule statut,

        @Schema(description = "Agence du véhicule (obligatoire pour POST /vehicules, ignoré quand l'agence est dans l'URL)",
                example = "1")
        Long idAgence,

        @Schema(description = "Ids d'équipements existants à affecter", example = "[1]")
        List<Long> equipementIds,

        @Schema(description = "Libellés de nouveaux équipements à créer et affecter", example = "[\"GPS\"]")
        List<@NotBlank @Size(max = 100) String> nouveauxEquipements
) {
}
