package tn.esprit.autoloc.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Création d'un véhicule avec ses équipements : le véhicule (validé comme VehiculeRequestDTO),
 * des équipements existants (par id) et/ou de nouveaux équipements (par libellé).
 */
public record VehiculeAvecEquipementsRequestDTO(

        @Valid
        @NotNull(message = "Le véhicule est obligatoire")
        VehiculeRequestDTO vehicule,

        @Schema(description = "Ids d'équipements existants", example = "[1]")
        List<Long> equipementIds,

        @Schema(description = "Libellés de nouveaux équipements à créer", example = "[\"GPS\"]")
        List<@NotBlank(message = "Le libellé d'un équipement ne peut pas être vide")
             @Size(max = 100) String> nouveauxEquipements
) {
}
