package bank.controllers;

import bank.models.Role;
import bank.models.Transactions.RegularTransaction;
import bank.models.Transactions.ThirdPartyTransaction;
import bank.models.Transactions.Transaction;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountRepository;
import bank.repositories.RoleRepository;
import bank.repositories.UserRepository;
import bank.services.ControllerService;
import bank.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    TransactionService transactionService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/hello-world")
    public String sayHi() {
        return "Hello";
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

    @GetMapping("/balance")
    @ResponseStatus(value = HttpStatus.OK)
    public String balance(@RequestParam Long id) {
        return "Balance of account " + id + " is: " + accountRepository.findById(id).get().getBalance();
    }

    @GetMapping("/balance/{username}") // Missing authentication
    @ResponseStatus(value = HttpStatus.OK)
    public List<String> findAllMyBalances(@PathVariable String username) {
        return controllerService.findAllMyBalances(username);
    }

    @PostMapping("/new-transaction")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Transaction newTransaction(@RequestBody RegularTransaction transaction) throws Exception {
        return transactionService.executeTransaction(transaction);
    }

    @PostMapping("/new-tptransaction")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Transaction newTransaction(@RequestBody ThirdPartyTransaction transaction,
                                      @RequestHeader("HashedKey") String hashedKey) throws Exception {
        return transactionService.executeTransaction(transaction, hashedKey);
    }
}


