package com.example.gestion_utilisateur.Controller;

import com.example.gestion_utilisateur.Entity.Role;
import com.example.gestion_utilisateur.Service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/add")
    public Role ajouter(@RequestBody Role role) {
        return roleService.ajouter(role);
    }

    @GetMapping("/getall")
    public List<Role> afficherTout() {
        return roleService.afficherTout();
    }

    @GetMapping("/getbyid/{id}")
    public Role afficherParId(@PathVariable Long id) {
        return roleService.afficherParId(id);
    }

    @PutMapping("/update")
    public Role modifier(@RequestBody Role role) {
        return roleService.modifier(role);
    }

    @DeleteMapping("/delete/{id}")
    public void supprimer(@PathVariable Long id) {
        roleService.supprimer(id);
    }
}