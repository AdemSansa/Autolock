package tn.esprit.autoloc.web.dto;

/**
 * DTO résumé : listes et résultats de recherche (l'essentiel pour identifier un véhicule).
 */
public record VehiculeResumeDTO(Long id, String immatriculation, String marque, String modele) {
}
