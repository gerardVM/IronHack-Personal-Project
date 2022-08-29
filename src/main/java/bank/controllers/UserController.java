package bank.controllers;

import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.repositories.RoleRepository;
import bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static bank.enums.Roles.ACCOUNT_HOLDER;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/say-hi")
    public String sayHi() {
        return "Hi";
    }

    @PostMapping("/create-user")
    public void createAccountHolder(@RequestBody AccountHolder accountHolder) {
        accountHolder.setPassword(passwordEncoder.encode(accountHolder.getPassword()));
        Role role = new Role();
        role.setRole(ACCOUNT_HOLDER);
        roleRepository.save(role);
        accountHolder.setRole(role);
        userRepository.save(accountHolder);
    }

}
