package tn.esprit.autoloc.web.dto;

/**
 * Résultat de J8 : nombre de véhicules par agence, construit directement par la requête JPQL.
 */
public record NbVehiculesAgenceDTO(String agenceNom, Long nbVehicules) {
}
