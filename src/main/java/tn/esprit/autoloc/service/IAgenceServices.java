package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Agence;

import java.util.List;

public interface IAgenceServices {
    Agence create(Agence agence);
    Agence findById(Long id);
    List<Agence> findAll();
    void deleteById(Long id);
    Agence update(Agence agence);
}