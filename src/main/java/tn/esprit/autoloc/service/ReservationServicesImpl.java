package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.repository.ReservationRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ReservationServicesImpl implements IReservationServices {

    private final ReservationRepository reservationRepository;

    @Override
    public Reservation create(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public Reservation update(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}