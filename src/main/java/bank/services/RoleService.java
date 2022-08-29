package bank.services;

import bank.enums.Roles;
import bank.models.Role;
import bank.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> findByRole(Roles role) {
        return roleRepository.findByRole(role);
    }
}
