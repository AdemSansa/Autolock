package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Equipement;

import java.util.List;

public interface IEquipementServices {
    Equipement create(Equipement equipement);
    Equipement findById(Long id);
    List<Equipement> findAll();
    void deleteById(Long id);
    Equipement update(Equipement equipement);
}