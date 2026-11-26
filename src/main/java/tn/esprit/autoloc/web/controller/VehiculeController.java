package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeServices;
import tn.esprit.autoloc.web.dto.vehicule.VehiculeMapper;
import tn.esprit.autoloc.web.dto.vehicule.VehiculeRequest;
import tn.esprit.autoloc.web.dto.vehicule.VehiculeResponse;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Vehicules", description = "Gestion des véhicules")
public class VehiculeController {

    private final IVehiculeServices vehiculeServices;
    private final VehiculeMapper vehiculeMapper;

    @Operation(summary = "Lister les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules")
    @GetMapping
    public List<VehiculeResponse> findAll() {
        return vehiculeMapper.toResponseList(vehiculeServices.findAll());
    }

    @Operation(summary = "Consulter un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/{id}")
    public VehiculeResponse findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return vehiculeMapper.toResponse(vehiculeServices.findById(id));
    }

    @Operation(summary = "Créer un véhicule (idAgence obligatoire dans le corps)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
            @ApiResponse(responseCode = "404", description = "Agence ou équipement introuvable")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeResponse create(@Valid @RequestBody VehiculeRequest request) {
        if (request.idAgence() == null) {
            throw new IllegalArgumentException("L'agence (idAgence) est obligatoire");
        }
        return createInAgence(request, request.idAgence());
    }

    @Operation(summary = "Modifier un véhicule",
            description = "Met à jour les champs du véhicule. Si idAgence est fourni, le véhicule est réaffecté. "
                    + "Les équipements se gèrent avec les endpoints /equipements.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou agence introuvable"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PutMapping("/{id}")
    public VehiculeResponse update(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id,
                                   @Valid @RequestBody VehiculeRequest request) {
        Vehicule vehicule = vehiculeServices.findById(id);
        vehiculeMapper.updateEntity(request, vehicule);
        vehicule = vehiculeServices.update(vehicule);
        if (request.idAgence() != null) {
            vehicule = vehiculeServices.assignToAgence(id, request.idAgence());
        }
        return vehiculeMapper.toResponse(vehicule);
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
    public VehiculeResponse addAndAssignToAgence(@Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence,
                                                 @Valid @RequestBody VehiculeRequest request) {
        return createInAgence(request, idAgence);
    }

    @Operation(summary = "Affecter un véhicule existant à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule affecté"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou agence introuvable")
    })
    @PutMapping("/{idVehicule}/agence/{idAgence}")
    public VehiculeResponse assignToAgence(@Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
                                           @Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence) {
        return vehiculeMapper.toResponse(vehiculeServices.assignToAgence(idVehicule, idAgence));
    }

    @Operation(summary = "Affecter un équipement à un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement affecté"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable")
    })
    @PutMapping("/{idVehicule}/equipements/{idEquipement}")
    public VehiculeResponse assignEquipement(@Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
                                             @Parameter(description = "Identifiant de l'équipement") @PathVariable Long idEquipement) {
        return vehiculeMapper.toResponse(vehiculeServices.assignEquipement(idVehicule, idEquipement));
    }

    @Operation(summary = "Retirer tous les équipements d'un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipements retirés"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @DeleteMapping("/{idVehicule}/equipements")
    public VehiculeResponse unassignEquipement(@Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule) {
        return vehiculeMapper.toResponse(vehiculeServices.unassignEquipement(idVehicule));
    }

    @Operation(summary = "Retirer un équipement d'un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement retiré"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable ou équipement non affecté")
    })
    @DeleteMapping("/{idVehicule}/equipements/{idEquipement}")
    public VehiculeResponse unassignEquipement(@Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
                                               @Parameter(description = "Identifiant de l'équipement") @PathVariable Long idEquipement) {
        return vehiculeMapper.toResponse(vehiculeServices.unassignEquipement(idVehicule, idEquipement));
    }

    @Operation(summary = "Créer un véhicule avec ses équipements et l'affecter à une agence",
            description = "equipementIds = équipements existants, nouveauxEquipements = libellés à créer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule et équipements créés, véhicule affecté"),
            @ApiResponse(responseCode = "404", description = "Agence ou équipement introuvable"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping("/equipements/agence/{idAgence}")
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeResponse addVehiculeAndEquipementsAndAssignToAgence(
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence,
            @Valid @RequestBody VehiculeRequest request) {
        return createInAgence(request, idAgence);
    }

    /**
     * Toutes les créations passent par le même service : il charge l'agence, résout les équipements
     * existants (par id) et crée les nouveaux (par libellé).
     */
    private VehiculeResponse createInAgence(VehiculeRequest request, Long idAgence) {
        Vehicule vehicule = vehiculeMapper.toEntity(request);
        return vehiculeMapper.toResponse(
                vehiculeServices.addVehiculeAndEquipementsAndAssignToAgence(vehicule, idAgence));
    }
}
