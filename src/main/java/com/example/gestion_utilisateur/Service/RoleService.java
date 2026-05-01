package com.example.gestion_utilisateur.Service;

import com.example.gestion_utilisateur.Entity.Role;
import com.example.gestion_utilisateur.Repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role ajouter(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> afficherTout() {
        return roleRepository.findAll();
    }

    public Role afficherParId(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role modifier(Role role) {
        return roleRepository.save(role);
    }

    public void supprimer(Long id) {
        roleRepository.deleteById(id);
    }
}