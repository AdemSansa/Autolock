package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Client;

import java.util.List;

public interface IClientServices {
    Client create(Client client);
    Client findById(Long id);
    List<Client> findAll();
    void deleteById(Long id);
    Client update(Client client);
}