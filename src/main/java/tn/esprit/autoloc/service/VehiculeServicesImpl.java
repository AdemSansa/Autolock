package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.EquipementRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO;
import tn.esprit.autoloc.web.dto.VehiculeAvecEquipementsRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.web.dto.VehiculeResumeDTO;
import tn.esprit.autoloc.web.exception.BusinessException;
import tn.esprit.autoloc.web.exception.ResourceNotFoundException;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehiculeServicesImpl implements IVehiculeServices {

    private final VehiculeRepository vehiculeRepository;
    private final AgenceRepository agenceRepository;
    private final EquipementRepository equipementRepository;
    private final VehiculeMapper vehiculeMapper;

    // ===================== CRUD =====================

    /**
     * J7 : agence et équipements chargés en une seule requête (au lieu de 1 + 2 requêtes paresseuses).
     */
    @Override
    public VehiculeResponseDTO findById(Long id) {
        Vehicule vehicule = vehiculeRepository.findByIdAvecEquipements(id)
                .orElseThrow(() -> vehiculeIntrouvable(id));
        return vehiculeMapper.toDto(vehicule);
    }

    @Override
    public List<VehiculeResumeDTO> findAll() {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findAll());
    }

    @Override
    @Transactional
    public VehiculeResponseDTO create(VehiculeRequestDTO dto) {
        // K6 : une immatriculation déjà utilisée est refusée
        verifierImmatriculationLibre(dto.immatriculation());
        // 1. DTO -> entité (id ignoré, statut = DISPONIBLE)
        Vehicule vehicule = vehiculeMapper.toEntity(dto);
        // 2. agence chargée par le service puis associée (méthode de synchronisation)
        findAgence(dto.agenceId()).addVehicule(vehicule);
        // 3. enregistrement puis DTO de réponse
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional
    public VehiculeResponseDTO update(Long id, VehiculeRequestDTO dto) {
        Vehicule vehicule = loadVehicule(id);
        if (vehiculeRepository.existsByImmatriculationAndIdVehiculeNot(dto.immatriculation(), id)) {
            throw new BusinessException("L'immatriculation " + dto.immatriculation() + " est déjà utilisée");
        }
        vehiculeMapper.updateEntity(dto, vehicule);
        findAgence(dto.agenceId()).addVehicule(vehicule);
        return vehiculeMapper.toDto(vehicule);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        vehiculeRepository.delete(loadVehicule(id));
    }

    // ===================== Affectations =====================

    /**
     * Crée un véhicule avec ses équipements : ceux donnés par id doivent exister, ceux donnés par libellé sont créés.
     */
    @Override
    @Transactional
    public VehiculeResponseDTO addVehiculeAndEquipements(VehiculeAvecEquipementsRequestDTO dto) {
        VehiculeRequestDTO v = dto.vehicule();
        verifierImmatriculationLibre(v.immatriculation());
        Vehicule vehicule = vehiculeMapper.toEntity(v);
        findAgence(v.agenceId()).addVehicule(vehicule);

        if (dto.equipementIds() != null) {
            for (Long idEquipement : dto.equipementIds()) {
                Equipement e = findEquipement(idEquipement);
                if (!vehicule.getEquipements().contains(e)) {
                    vehicule.getEquipements().add(e);
                }
            }
        }
        if (dto.nouveauxEquipements() != null) {
            for (String libelle : dto.nouveauxEquipements()) {
                Equipement e = new Equipement();
                e.setLibelle(libelle);
                vehicule.getEquipements().add(equipementRepository.save(e));
            }
        }
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional
    public VehiculeResponseDTO assignToAgence(Long idVehicule, Long idAgence) {
        Vehicule vehicule = loadVehicule(idVehicule);
        findAgence(idAgence).addVehicule(vehicule);
        return vehiculeMapper.toDto(vehicule);
    }

    /**
     * Relation ManyToMany, côté propriétaire : Vehicule.
     */
    @Override
    @Transactional
    public VehiculeResponseDTO assignEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = loadVehicule(idVehicule);
        Equipement equipement = findEquipement(idEquipement);
        if (!vehicule.getEquipements().contains(equipement)) {
            vehicule.getEquipements().add(equipement);
        }
        return vehiculeMapper.toDto(vehicule);
    }

    @Override
    @Transactional
    public VehiculeResponseDTO unassignEquipement(Long idVehicule) {
        Vehicule vehicule = loadVehicule(idVehicule);
        vehicule.getEquipements().clear();
        return vehiculeMapper.toDto(vehicule);
    }

    @Override
    @Transactional
    public VehiculeResponseDTO unassignEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = loadVehicule(idVehicule);
        boolean removed = vehicule.getEquipements().removeIf(e -> e.getIdEquipement().equals(idEquipement));
        if (!removed) {
            throw new ResourceNotFoundException("L'équipement " + idEquipement
                    + " n'est pas affecté au véhicule " + idVehicule);
        }
        return vehiculeMapper.toDto(vehicule);
    }

    // ===================== Keywords (K1 à K10) =====================

    @Override
    public List<VehiculeResumeDTO> findByMarque(String marque) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findByMarque(marque));
    }

    @Override
    public List<VehiculeResumeDTO> findByCategorieEtStatut(CategorieVehicule categorie, StatutVehicule statut) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findByCategorieAndStatut(categorie, statut));
    }

    @Override
    public List<VehiculeResumeDTO> findByTarifEntre(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Le tarif minimum doit être inférieur ou égal au tarif maximum");
        }
        return vehiculeMapper.toResumeDtoList(
                vehiculeRepository.findByTarifJournalierBetweenOrderByTarifJournalierAsc(min, max));
    }

    @Override
    public List<VehiculeResumeDTO> findByModeleContenant(String motCle) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findByModeleContainingIgnoreCase(motCle));
    }

    @Override
    public List<VehiculeResumeDTO> findByCategories(List<CategorieVehicule> categories) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findByCategorieIn(categories));
    }

    @Override
    public boolean existeImmatriculation(String immatriculation) {
        return vehiculeRepository.existsByImmatriculation(immatriculation);
    }

    @Override
    public long compterParStatut(StatutVehicule statut) {
        return vehiculeRepository.countByStatut(statut);
    }

    @Override
    public List<VehiculeResumeDTO> findTop3MoinsChers() {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findTop3ByOrderByTarifJournalierAsc());
    }

    @Override
    public List<VehiculeResumeDTO> findByAgence(Long agenceId) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findByAgenceIdAgence(agenceId));
    }

    @Override
    public List<VehiculeResumeDTO> findSansAgence() {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findByAgenceIsNull());
    }

    // ===================== Jointures JPQL (J1 à J8) =====================

    @Override
    public List<VehiculeResumeDTO> findByVilleAgence(String ville) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findByVilleAgence(ville));
    }

    @Override
    public List<VehiculeResumeDTO> findByEquipement(String libelle) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findByEquipement(libelle));
    }

    @Override
    public List<VehiculeResumeDTO> findAvecAuMoinsNEquipements(long n) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findAvecAuMoinsNEquipements(n));
    }

    @Override
    public List<VehiculeResumeDTO> findReservesParClient(Long clientId) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findReservesParClient(clientId));
    }

    @Override
    public List<VehiculeResumeDTO> findJamaisReserves() {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findJamaisReserves());
    }

    @Override
    public List<VehiculeResumeDTO> findEnMaintenanceLe(LocalDate date) {
        return vehiculeMapper.toResumeDtoList(vehiculeRepository.findEnMaintenanceLe(date));
    }

    @Override
    public List<NbVehiculesAgenceDTO> compterParAgence() {
        return vehiculeRepository.compterParAgence();
    }

    // ===================== Utilitaires =====================

    private Vehicule loadVehicule(Long id) {
        return vehiculeRepository.findById(id).orElseThrow(() -> vehiculeIntrouvable(id));
    }

    private ResourceNotFoundException vehiculeIntrouvable(Long id) {
        return new ResourceNotFoundException("Véhicule introuvable avec l'id : " + id);
    }

    private Agence findAgence(Long idAgence) {
        return agenceRepository.findById(idAgence)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable avec l'id : " + idAgence));
    }

    private Equipement findEquipement(Long idEquipement) {
        return equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement introuvable avec l'id : " + idEquipement));
    }

    private void verifierImmatriculationLibre(String immatriculation) {
        if (vehiculeRepository.existsByImmatriculation(immatriculation)) {
            throw new BusinessException("L'immatriculation " + immatriculation + " est déjà utilisée");
        }
    }
}
