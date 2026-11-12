package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeServices;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Vehicules", description = "Gestion des véhicules")
public class VehiculeController {

    private final IVehiculeServices vehiculeServices;

    @Operation(summary = "Lister les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules")
    @GetMapping
    public List<Vehicule> findAll() {
        return vehiculeServices.findAll();
    }

    @Operation(summary = "Consulter un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/{id}")
    public Vehicule findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return vehiculeServices.findById(id);
    }

    @Operation(summary = "Créer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicule create(@RequestBody Vehicule vehicule) {
        validate(vehicule);
        return vehiculeServices.create(vehicule);
    }

    @Operation(summary = "Modifier un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PutMapping("/{id}")
    public Vehicule update(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id,
                           @RequestBody Vehicule vehicule) {
        validate(vehicule);
        vehiculeServices.findById(id);
        vehicule.setIdVehicule(id);
        return vehiculeServices.update(vehicule);
    }

    @Operation(summary = "Supprimer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeServices.findById(id);
        vehiculeServices.deleteById(id);
    }

    // ---------- Affectations ----------

    @Operation(summary = "Créer un véhicule et l'affecter à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé et affecté"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping("/agence/{idAgence}")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicule addAndAssignToAgence(@Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence,
                                         @RequestBody Vehicule vehicule) {
        validate(vehicule);
        return vehiculeServices.addAndAssignToAgence(vehicule, idAgence);
    }

    @Operation(summary = "Affecter un véhicule existant à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule affecté"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou agence introuvable")
    })
    @PutMapping("/{idVehicule}/agence/{idAgence}")
    public Vehicule assignToAgence(@Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
                                   @Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence) {
        return vehiculeServices.assignToAgence(idVehicule, idAgence);
    }

    @Operation(summary = "Affecter un équipement à un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement affecté"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable")
    })
    @PutMapping("/{idVehicule}/equipements/{idEquipement}")
    public Vehicule assignEquipement(@Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
                                     @Parameter(description = "Identifiant de l'équipement") @PathVariable Long idEquipement) {
        return vehiculeServices.assignEquipement(idVehicule, idEquipement);
    }

    @Operation(summary = "Retirer tous les équipements d'un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipements retirés"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @DeleteMapping("/{idVehicule}/equipements")
    public Vehicule unassignEquipement(@Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule) {
        return vehiculeServices.unassignEquipement(idVehicule);
    }

    @Operation(summary = "Retirer un équipement d'un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement retiré"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable ou équipement non affecté")
    })
    @DeleteMapping("/{idVehicule}/equipements/{idEquipement}")
    public Vehicule unassignEquipement(@Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
                                       @Parameter(description = "Identifiant de l'équipement") @PathVariable Long idEquipement) {
        return vehiculeServices.unassignEquipement(idVehicule, idEquipement);
    }

    @Operation(summary = "Créer un véhicule avec ses équipements et l'affecter à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule et équipements créés, véhicule affecté"),
            @ApiResponse(responseCode = "404", description = "Agence ou équipement introuvable"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping("/equipements/agence/{idAgence}")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicule addVehiculeAndEquipementsAndAssignToAgence(
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence,
            @RequestBody Vehicule vehicule) {
        validate(vehicule);
        return vehiculeServices.addVehiculeAndEquipementsAndAssignToAgence(vehicule, idAgence);
    }

    private void validate(Vehicule vehicule) {
        BigDecimal tarif = vehicule.getTarifJournalier();
        if (tarif != null && tarif.signum() < 0) {
            throw new IllegalArgumentException("Le tarif journalier ne peut pas être négatif");
        }
    }
}
