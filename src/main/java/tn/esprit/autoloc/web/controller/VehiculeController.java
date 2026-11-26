package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.service.IVehiculeServices;
import tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO;
import tn.esprit.autoloc.web.dto.VehiculeAvecEquipementsRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.web.dto.VehiculeResumeDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Vehicules", description = "Gestion des véhicules")
public class VehiculeController {

    private final IVehiculeServices vehiculeServices;

    // ===================== CRUD =====================

    @Operation(summary = "Lister les véhicules (format résumé)")
    @GetMapping
    public List<VehiculeResumeDTO> findAll() {
        return vehiculeServices.findAll();
    }

    @Operation(summary = "Consulter un véhicule (agence et équipements chargés en une requête, J7)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/{id}")
    public VehiculeResponseDTO findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return vehiculeServices.findById(id);
    }

    @Operation(summary = "Créer un véhicule (statut DISPONIBLE, affecté à l'agence agenceId)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable"),
            @ApiResponse(responseCode = "409", description = "Immatriculation déjà utilisée")
    })
    @PostMapping
    public ResponseEntity<VehiculeResponseDTO> create(@Valid @RequestBody VehiculeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeServices.create(dto));
    }

    @Operation(summary = "Modifier un véhicule (le statut et les équipements ne changent pas)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou agence introuvable"),
            @ApiResponse(responseCode = "409", description = "Immatriculation déjà utilisée")
    })
    @PutMapping("/{id}")
    public VehiculeResponseDTO update(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id,
                                      @Valid @RequestBody VehiculeRequestDTO dto) {
        return vehiculeServices.update(id, dto);
    }

    @Operation(summary = "Supprimer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeServices.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== Affectations =====================

    @Operation(summary = "Créer un véhicule avec ses équipements (existants par id, nouveaux par libellé)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule et équipements créés"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Agence ou équipement introuvable"),
            @ApiResponse(responseCode = "409", description = "Immatriculation déjà utilisée")
    })
    @PostMapping("/avec-equipements")
    public ResponseEntity<VehiculeResponseDTO> addVehiculeAndEquipements(
            @Valid @RequestBody VehiculeAvecEquipementsRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeServices.addVehiculeAndEquipements(dto));
    }

    @Operation(summary = "Affecter un véhicule existant à une agence")
    @PutMapping("/{idVehicule}/agence/{idAgence}")
    public VehiculeResponseDTO assignToAgence(@PathVariable Long idVehicule, @PathVariable Long idAgence) {
        return vehiculeServices.assignToAgence(idVehicule, idAgence);
    }

    @Operation(summary = "Affecter un équipement à un véhicule")
    @PutMapping("/{idVehicule}/equipements/{idEquipement}")
    public VehiculeResponseDTO assignEquipement(@PathVariable Long idVehicule, @PathVariable Long idEquipement) {
        return vehiculeServices.assignEquipement(idVehicule, idEquipement);
    }

    @Operation(summary = "Retirer tous les équipements d'un véhicule")
    @DeleteMapping("/{idVehicule}/equipements")
    public VehiculeResponseDTO unassignEquipement(@PathVariable Long idVehicule) {
        return vehiculeServices.unassignEquipement(idVehicule);
    }

    @Operation(summary = "Retirer un équipement d'un véhicule")
    @DeleteMapping("/{idVehicule}/equipements/{idEquipement}")
    public VehiculeResponseDTO unassignEquipement(@PathVariable Long idVehicule, @PathVariable Long idEquipement) {
        return vehiculeServices.unassignEquipement(idVehicule, idEquipement);
    }

    // ===================== Étape 6 — Keywords (K1 à K10) =====================

    @Operation(summary = "K1 — véhicules d'une marque")
    @GetMapping("/recherche/marque/{marque}")
    public List<VehiculeResumeDTO> findByMarque(@PathVariable String marque) {
        return vehiculeServices.findByMarque(marque);
    }

    @Operation(summary = "K2 — véhicules d'une catégorie et d'un statut")
    @GetMapping("/recherche/categorie/{categorie}/statut/{statut}")
    public List<VehiculeResumeDTO> findByCategorieEtStatut(@PathVariable CategorieVehicule categorie,
                                                           @PathVariable StatutVehicule statut) {
        return vehiculeServices.findByCategorieEtStatut(categorie, statut);
    }

    @Operation(summary = "K3 — tarif entre min et max, triés par tarif croissant")
    @GetMapping("/recherche/tarif/{min}/{max}")
    public List<VehiculeResumeDTO> findByTarifEntre(@PathVariable BigDecimal min, @PathVariable BigDecimal max) {
        return vehiculeServices.findByTarifEntre(min, max);
    }

    @Operation(summary = "K4 — modèle contenant un mot-clé (insensible à la casse)")
    @GetMapping("/recherche/modele/{motCle}")
    public List<VehiculeResumeDTO> findByModeleContenant(@PathVariable String motCle) {
        return vehiculeServices.findByModeleContenant(motCle);
    }

    @Operation(summary = "K5 — véhicules d'une liste de catégories (séparées par des virgules, ex. SUV,BERLINE)")
    @GetMapping("/recherche/categories/{categories}")
    public List<VehiculeResumeDTO> findByCategories(@PathVariable List<CategorieVehicule> categories) {
        return vehiculeServices.findByCategories(categories);
    }

    @Operation(summary = "K6 — une immatriculation existe-t-elle déjà ?")
    @GetMapping("/recherche/existe/{immatriculation}")
    public boolean existeImmatriculation(@PathVariable String immatriculation) {
        return vehiculeServices.existeImmatriculation(immatriculation);
    }

    @Operation(summary = "K7 — nombre de véhicules dans un statut")
    @GetMapping("/recherche/nombre/statut/{statut}")
    public long compterParStatut(@PathVariable StatutVehicule statut) {
        return vehiculeServices.compterParStatut(statut);
    }

    @Operation(summary = "K8 — les 3 véhicules les moins chers")
    @GetMapping("/recherche/moins-chers")
    public List<VehiculeResumeDTO> findTop3MoinsChers() {
        return vehiculeServices.findTop3MoinsChers();
    }

    @Operation(summary = "K9 — véhicules d'une agence")
    @GetMapping("/recherche/agence/{agenceId}")
    public List<VehiculeResumeDTO> findByAgence(@PathVariable Long agenceId) {
        return vehiculeServices.findByAgence(agenceId);
    }

    @Operation(summary = "K10 — véhicules sans agence")
    @GetMapping("/recherche/sans-agence")
    public List<VehiculeResumeDTO> findSansAgence() {
        return vehiculeServices.findSansAgence();
    }

    // ===================== Étape 7 — Jointures JPQL (J1 à J8) =====================

    @Operation(summary = "J1 — véhicules des agences d'une ville")
    @GetMapping("/ville/{ville}")
    public List<VehiculeResumeDTO> findByVilleAgence(@PathVariable String ville) {
        return vehiculeServices.findByVilleAgence(ville);
    }

    @Operation(summary = "J2 — véhicules possédant un équipement donné (libellé)")
    @GetMapping("/equipement/{libelle}")
    public List<VehiculeResumeDTO> findByEquipement(@PathVariable String libelle) {
        return vehiculeServices.findByEquipement(libelle);
    }

    @Operation(summary = "J3 — véhicules possédant au moins N équipements")
    @GetMapping("/equipements/min/{n}")
    public List<VehiculeResumeDTO> findAvecAuMoinsNEquipements(@PathVariable long n) {
        return vehiculeServices.findAvecAuMoinsNEquipements(n);
    }

    @Operation(summary = "J4 — véhicules déjà réservés par un client")
    @GetMapping("/client/{clientId}")
    public List<VehiculeResumeDTO> findReservesParClient(@PathVariable Long clientId) {
        return vehiculeServices.findReservesParClient(clientId);
    }

    @Operation(summary = "J5 — véhicules jamais réservés")
    @GetMapping("/jamais-reserves")
    public List<VehiculeResumeDTO> findJamaisReserves() {
        return vehiculeServices.findJamaisReserves();
    }

    @Operation(summary = "J6 — véhicules en maintenance à une date (format AAAA-MM-JJ)")
    @GetMapping("/maintenance/{date}")
    public List<VehiculeResumeDTO> findEnMaintenanceLe(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return vehiculeServices.findEnMaintenanceLe(date);
    }

    @Operation(summary = "J8 — nombre de véhicules par agence")
    @GetMapping("/stats/par-agence")
    public List<NbVehiculesAgenceDTO> compterParAgence() {
        return vehiculeServices.compterParAgence();
    }
}
