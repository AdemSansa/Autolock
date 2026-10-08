package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.repository.ClientRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ClientServicesImpl implements IClientServices {

    private final ClientRepository clientRepository;

    @Override
    public Client create(Client client) {
        return clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public Client update(Client client) {
        return clientRepository.save(client);
    }
}