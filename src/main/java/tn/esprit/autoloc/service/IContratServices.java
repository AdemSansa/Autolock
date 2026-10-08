package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Contrat;

import java.util.List;

public interface IContratServices {
    Contrat create(Contrat contrat);
    Contrat findById(Long id);
    List<Contrat> findAll();
    void deleteById(Long id);
    Contrat update(Contrat contrat);
}