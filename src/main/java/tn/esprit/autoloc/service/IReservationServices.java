package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Reservation;

import java.util.List;

public interface IReservationServices {
    Reservation create(Reservation reservation);
    Reservation findById(Long id);
    List<Reservation> findAll();
    void deleteById(Long id);
    Reservation update(Reservation reservation);
}