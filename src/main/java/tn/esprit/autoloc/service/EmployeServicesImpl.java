package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.repository.EmployeRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class EmployeServicesImpl implements IEmployeServices {

    private final EmployeRepository employeRepository;

    @Override
    public Employe create(Employe employe) {
        return employeRepository.save(employe);
    }

    @Override
    @Transactional(readOnly = true)
    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé introuvable avec l'id : " + id));
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
}