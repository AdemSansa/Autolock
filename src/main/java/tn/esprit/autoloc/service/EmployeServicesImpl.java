package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.EmployeRepository;
import tn.esprit.autoloc.web.exception.ResourceNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class EmployeServicesImpl implements IEmployeServices {

    private final EmployeRepository employeRepository;
    private final AgenceRepository agenceRepository;

    @Override
    public Employe create(Employe employe) {
        return employeRepository.save(employe);
    }

    @Override
    @Transactional(readOnly = true)
    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        employeRepository.deleteById(id);
    }

    @Override
    public Employe update(Employe employe) {
        return employeRepository.save(employe);
    }

    // ---------- Affectations ----------

    /**
     * Crée un employé et l'affecte directement à une agence existante.
     */
    @Override
    public Employe addAndAssignToAgence(Employe employe, Long idAgence) {
        employe.setAgence(findAgence(idAgence));
        return employeRepository.save(employe);
    }

    /**
     * Affecte (ou mute) un employé existant vers une agence existante.
     */
    @Override
    public Employe assignToAgence(Long idEmploye, Long idAgence) {
        Employe employe = findById(idEmploye);
        employe.setAgence(findAgence(idAgence));
        return employeRepository.save(employe);
    }

    private Agence findAgence(Long idAgence) {
        return agenceRepository.findById(idAgence)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable avec l'id : " + idAgence));
    }
}
