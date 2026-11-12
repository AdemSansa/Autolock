package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.web.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ContratServicesImpl implements IContratServices {

    private final ContratRepository contratRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public Contrat create(Contrat contrat) {
        return contratRepository.save(contrat);
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat introuvable avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        contratRepository.deleteById(id);
    }

    @Override
    public Contrat update(Contrat contrat) {
        return contratRepository.save(contrat);
    }

    /**
     * Crée un contrat et ses paiements en une seule opération.
     * Les paiements sont enregistrés grâce au cascade = ALL de Contrat.paiements ;
     * il faut seulement renseigner le côté propriétaire (Paiement.contrat).
     */
    @Override
    public Contrat addWithPayments(Contrat contrat) {
        if (contrat.getReservation() == null || contrat.getReservation().getIdReservation() == null) {
            throw new IllegalArgumentException("Le contrat doit référencer une réservation existante (idReservation)");
        }
        Long idReservation = contrat.getReservation().getIdReservation();
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec l'id : " + idReservation));
        if (reservation.getContrat() != null) {
            throw new IllegalArgumentException("La réservation " + idReservation + " a déjà un contrat");
        }
        contrat.setReservation(reservation);

        if (contrat.getPaiements() != null) {
            for (Paiement paiement : contrat.getPaiements()) {
                paiement.setContrat(contrat);
                if (paiement.getDatePaiement() == null) {
                    paiement.setDatePaiement(LocalDateTime.now());
                }
            }
        }
        return contratRepository.save(contrat);
    }

    /**
     * Ajoute un paiement à un contrat existant (cascade = ALL : le paiement est enregistré avec le contrat).
     */
    @Override
    public Contrat addPaiementToContrat(Long idContrat, Paiement paiement) {
        Contrat contrat = findById(idContrat);
        if (paiement.getMontant() == null || paiement.getMontant().signum() <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit être positif");
        }
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(LocalDateTime.now());
        }
        paiement.setContrat(contrat);
        contrat.getPaiements().add(paiement);
        return contratRepository.save(contrat);
    }
}
