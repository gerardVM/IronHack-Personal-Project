package bank.services;

import bank.models.roles.AccountHolder;
import bank.models.roles.Admin;
import bank.repositories.AccountHolderRepository;
import bank.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
}
