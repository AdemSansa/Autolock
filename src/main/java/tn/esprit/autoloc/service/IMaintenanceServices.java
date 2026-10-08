package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Maintenance;

import java.util.List;

public interface IMaintenanceServices {
    Maintenance create(Maintenance maintenance);
    Maintenance findById(Long id);
    List<Maintenance> findAll();
    void deleteById(Long id);
    Maintenance update(Maintenance maintenance);
}