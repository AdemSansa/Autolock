package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO;
import tn.esprit.autoloc.web.dto.VehiculeAvecEquipementsRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.web.dto.VehiculeResumeDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Le service reçoit et renvoie des DTO : la conversion entité <-> DTO se fait dans sa transaction.
 */
public interface IVehiculeServices {

    // CRUD
    VehiculeResponseDTO findById(Long id);
    List<VehiculeResumeDTO> findAll();
    VehiculeResponseDTO create(VehiculeRequestDTO dto);
    VehiculeResponseDTO update(Long id, VehiculeRequestDTO dto);
    void deleteById(Long id);

    // Affectations
    VehiculeResponseDTO addVehiculeAndEquipements(VehiculeAvecEquipementsRequestDTO dto);
    VehiculeResponseDTO assignToAgence(Long idVehicule, Long idAgence);
    VehiculeResponseDTO assignEquipement(Long idVehicule, Long idEquipement);
    VehiculeResponseDTO unassignEquipement(Long idVehicule);
    VehiculeResponseDTO unassignEquipement(Long idVehicule, Long idEquipement);

    // Keywords (K1 à K10)
    List<VehiculeResumeDTO> findByMarque(String marque);
    List<VehiculeResumeDTO> findByCategorieEtStatut(CategorieVehicule categorie, StatutVehicule statut);
    List<VehiculeResumeDTO> findByTarifEntre(BigDecimal min, BigDecimal max);
    List<VehiculeResumeDTO> findByModeleContenant(String motCle);
    List<VehiculeResumeDTO> findByCategories(List<CategorieVehicule> categories);
    boolean existeImmatriculation(String immatriculation);
    long compterParStatut(StatutVehicule statut);
    List<VehiculeResumeDTO> findTop3MoinsChers();
    List<VehiculeResumeDTO> findByAgence(Long agenceId);
    List<VehiculeResumeDTO> findSansAgence();

    // Jointures JPQL (J1 à J8 ; J7 est utilisée par findById)
    List<VehiculeResumeDTO> findByVilleAgence(String ville);
    List<VehiculeResumeDTO> findByEquipement(String libelle);
    List<VehiculeResumeDTO> findAvecAuMoinsNEquipements(long n);
    List<VehiculeResumeDTO> findReservesParClient(Long clientId);
    List<VehiculeResumeDTO> findJamaisReserves();
    List<VehiculeResumeDTO> findEnMaintenanceLe(LocalDate date);
    List<NbVehiculesAgenceDTO> compterParAgence();
}
