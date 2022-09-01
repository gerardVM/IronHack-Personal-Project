package bank.controllers;

import bank.models.Role;
import bank.models.Transactions.RegularTransaction;
import bank.models.Transactions.ThirdPartyTransaction;
import bank.models.Transactions.Transaction;
import bank.models.roles.AccountHolder;
import bank.models.roles.Admin;
import bank.models.roles.ThirdParty;
import bank.repositories.AccountRepository;
import bank.repositories.RoleRepository;
import bank.repositories.UserRepository;
import bank.services.ControllerService;
import bank.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static bank.enums.Roles.*;

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
    @ResponseStatus(HttpStatus.OK)
    public String sayHi() {
        return "Hello";
    }

    @PostMapping("/create-account-holder")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccountHolder(@RequestBody AccountHolder accountHolder) {
        accountHolder.setPassword(passwordEncoder.encode(accountHolder.getPassword()));
        Role role = new Role();
        role.setRole(ACCOUNT_HOLDER);
        roleRepository.save(role);
        accountHolder.setRole(role);
        userRepository.save(accountHolder);
    }

    @PostMapping("/create-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAdmin(@RequestBody Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        Role role = new Role();
        role.setRole(ADMIN);
        roleRepository.save(role);
        admin.setRole(role);
        userRepository.save(admin);
    }

    @PostMapping("/create-third-party")
    @ResponseStatus(HttpStatus.CREATED)
    public void createThirdParty(@RequestBody ThirdParty thirdParty) {
        thirdParty.setPassword(passwordEncoder.encode(thirdParty.getPassword()));
        Role role = new Role();
        role.setRole(THIRD_PARTY);
        roleRepository.save(role);
        thirdParty.setRole(role);
        userRepository.save(thirdParty);
    }

    @GetMapping("/balance")
    @ResponseStatus(value = HttpStatus.OK)
    public String balance(@RequestParam Long id) {
        return "Balance of account " + id + " is: " + accountRepository.findById(id).get().getBalance();
    }

    @PatchMapping("/modify-balance")
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String modifyBalance(@RequestParam Long id, @RequestParam BigDecimal amount) throws Exception {
        return controllerService.modifyBalance(id, amount);
    }

    @GetMapping("/balance/{accountId}")
    @ResponseStatus(value = HttpStatus.OK)
    public String findMyBalances(@PathVariable Long accountId, @AuthenticationPrincipal UserDetails userDetails) {
        return controllerService.findMyBalances(accountId, userDetails);
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


