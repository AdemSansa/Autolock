package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Paiement;

import java.util.List;

public interface IPaiementServices {
    Paiement create(Paiement paiement);
    Paiement findById(Long id);
    List<Paiement> findAll();
    void deleteById(Long id);
    Paiement update(Paiement paiement);
}