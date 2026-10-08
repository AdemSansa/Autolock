package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Employe;

import java.util.List;

public interface IEmployeServices {
    Employe create(Employe employe);
    Employe findById(Long id);
    List<Employe> findAll();
    void deleteById(Long id);
    Employe update(Employe employe);
}