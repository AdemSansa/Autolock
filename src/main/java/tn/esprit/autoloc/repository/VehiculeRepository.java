package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    // ===================== Étape 6 — Keywords =====================

    // K1 — véhicules d'une marque
    List<Vehicule> findByMarque(String marque);

    // K2 — véhicules d'une catégorie ET d'un statut
    List<Vehicule> findByCategorieAndStatut(CategorieVehicule categorie, StatutVehicule statut);

    // K3 — tarif entre min et max, triés par tarif croissant
    List<Vehicule> findByTarifJournalierBetweenOrderByTarifJournalierAsc(BigDecimal min, BigDecimal max);

    // K4 — modèle contenant un mot-clé, sans tenir compte de la casse
    List<Vehicule> findByModeleContainingIgnoreCase(String motCle);

    // K5 — véhicules appartenant à une liste de catégories
    List<Vehicule> findByCategorieIn(List<CategorieVehicule> categories);

    // K6 — une immatriculation existe-t-elle déjà ?
    boolean existsByImmatriculation(String immatriculation);

    // K6 bis — même contrôle à la modification (en excluant le véhicule modifié)
    boolean existsByImmatriculationAndIdVehiculeNot(String immatriculation, Long idVehicule);

    // K7 — nombre de véhicules dans un statut
    long countByStatut(StatutVehicule statut);

    // K8 — les 3 véhicules les moins chers
    List<Vehicule> findTop3ByOrderByTarifJournalierAsc();

    // K9 — véhicules d'une agence (par son id)
    List<Vehicule> findByAgenceIdAgence(Long agenceId);

    // K10 — véhicules sans agence
    List<Vehicule> findByAgenceIsNull();

    // ===================== Étape 7 — Jointures JPQL =====================

    // J1 — véhicules des agences d'une ville
    @Query("SELECT v FROM Vehicule v JOIN v.agence a WHERE a.ville = :ville")
    List<Vehicule> findByVilleAgence(@Param("ville") String ville);

    // J2 — véhicules possédant un équipement donné
    @Query("SELECT DISTINCT v FROM Vehicule v JOIN v.equipements e WHERE e.libelle = :libelle")
    List<Vehicule> findByEquipement(@Param("libelle") String libelle);

    // J3 — véhicules possédant au moins N équipements
    @Query("""
            SELECT v FROM Vehicule v JOIN v.equipements e
            GROUP BY v
            HAVING COUNT(e) >= :n
            """)
    List<Vehicule> findAvecAuMoinsNEquipements(@Param("n") long n);

    // J4 — véhicules déjà réservés par un client (Vehicule -> Reservation -> Client)
    @Query("""
            SELECT DISTINCT v FROM Vehicule v
            JOIN v.reservations r
            JOIN r.client c
            WHERE c.idClient = :clientId
            """)
    List<Vehicule> findReservesParClient(@Param("clientId") Long clientId);

    // J5 — véhicules jamais réservés
    @Query("SELECT v FROM Vehicule v LEFT JOIN v.reservations r WHERE r IS NULL")
    List<Vehicule> findJamaisReserves();

    // J6 — véhicules en maintenance à une date (maintenance commencée et pas encore terminée)
    @Query("""
            SELECT DISTINCT v FROM Vehicule v
            JOIN v.maintenances m
            WHERE m.dateDebut <= :date
              AND (m.dateFin IS NULL OR m.dateFin >= :date)
            """)
    List<Vehicule> findEnMaintenanceLe(@Param("date") LocalDate date);

    // J7 — un véhicule avec son agence et ses équipements en une seule requête
    @Query("""
            SELECT v FROM Vehicule v
            LEFT JOIN FETCH v.agence
            LEFT JOIN FETCH v.equipements
            WHERE v.idVehicule = :id
            """)
    Optional<Vehicule> findByIdAvecEquipements(@Param("id") Long id);

    // J8 — nombre de véhicules par agence, directement en DTO
    @Query("""
            SELECT new tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO(a.nom, COUNT(v))
            FROM Agence a LEFT JOIN a.vehicules v
            GROUP BY a.idAgence, a.nom
            """)
    List<NbVehiculesAgenceDTO> compterParAgence();
}
