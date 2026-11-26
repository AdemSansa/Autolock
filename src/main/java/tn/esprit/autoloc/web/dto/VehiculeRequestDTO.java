package tn.esprit.autoloc.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import tn.esprit.autoloc.domain.CategorieVehicule;

import java.math.BigDecimal;

/**
 * DTO de requête : données saisissables d'un véhicule (pas d'id, pas de statut) + identifiant de l'agence.
 */
public record VehiculeRequestDTO(

        @Schema(example = "120TU1001")
        @NotBlank(message = "L'immatriculation est obligatoire")
        // "^$|" : une chaîne vide n'est signalée que par @NotBlank (un seul message clair)
        @Pattern(regexp = "^$|^\\d{1,3} ?TU ?\\d{1,4}$",
                message = "Format d'immatriculation invalide (attendu : 120TU1001)")
        String immatriculation,

        @Schema(example = "Toyota")
        @NotBlank(message = "La marque est obligatoire")
        @Size(max = 30, message = "La marque ne doit pas dépasser 30 caractères")
        String marque,

        @Schema(example = "Yaris")
        @NotBlank(message = "Le modèle est obligatoire")
        @Size(max = 30, message = "Le modèle ne doit pas dépasser 30 caractères")
        String modele,

        @Schema(example = "CITADINE")
        @NotNull(message = "La catégorie est obligatoire")
        CategorieVehicule categorie,

        @Schema(example = "90.00")
        @NotNull(message = "Le tarif journalier est obligatoire")
        @Positive(message = "Le tarif journalier doit être strictement positif")
        BigDecimal tarifJournalier,

        @Schema(example = "1")
        @NotNull(message = "L'agence est obligatoire")
        Long agenceId
) {
}
