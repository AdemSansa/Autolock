package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Client;

import java.time.LocalDate;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByReservations_DateDebutLessThanEqualAndReservations_DateFinGreaterThanEqual(
            LocalDate dateFin,
            LocalDate dateDebut
    );
}