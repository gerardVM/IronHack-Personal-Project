package bank.controllers;

import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountRepository;
import bank.repositories.RoleRepository;
import bank.repositories.UserRepository;
import bank.services.ControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static bank.enums.Roles.ACCOUNT_HOLDER;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ControllerService controllerService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/say-hi")
    public String sayHi() {
        return "Hi";
    }

    @GetMapping("/balance")
    @ResponseStatus(value = HttpStatus.OK)
    public String balance(@RequestParam Long id) {
        return "Balance of account " + id + " is: " + accountRepository.findById(id).get().getBalance();
    }

    @GetMapping("/balance/{username}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<String> findAllMyBalances(@PathVariable String username) {
        return controllerService.findAllMyBalances(username);
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
