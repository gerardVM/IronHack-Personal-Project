package bank.services;

import bank.models.Transactions.RegularTransaction;
import bank.models.Transactions.Transaction;
import bank.models.User;
import bank.models.accounts.*;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountRepository;
import bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static bank.enums.Status.*;

@Service
public class ControllerService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    TransactionService transactionService;

    public String findMyBalances(Long accountId, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        List<Account> accounts = accountRepository.findAllByPrimaryOwner(user);
        accounts.addAll(accountRepository.findAllBySecondaryOwner(user));
        for (Account account : accounts) {
            if (account.getId().equals(accountId)) {
                return "Balance of your account " + account.getId() + " is: " + account.getBalance();
            }
        } return "Account not found";
    }

    public String modifyBalance(Long accountId, BigDecimal amount) throws Exception {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new IllegalArgumentException("Account not found")
        );
        // Creating a virtual user and account to register the transaction
        AccountHolder virtualAccountHolder = new AccountHolder();
        String randNum = String.valueOf((int) (Math.random() * 200 + 100));
        virtualAccountHolder.setUsername("bankUser/" + randNum);
        virtualAccountHolder.setPassword(passwordEncoder.encode("masterPassword"));
        virtualAccountHolder.setBirthDate(LocalDate.of(1990, 1, 1));
        userRepository.save(virtualAccountHolder);
        Checking virtualChecking = new Checking();
        boolean fromUserToBank = account.getBalance().compareTo(amount)>0;
        virtualChecking.setPenaltyApplied(true);
        virtualChecking.setBalance(fromUserToBank?BigDecimal.valueOf(0):(amount).subtract(account.getBalance()));
        virtualChecking.setSecretKey(passwordEncoder.encode("bankbank"));
        virtualChecking.setPrimaryOwner(virtualAccountHolder);
        virtualChecking.setAccountStatus(INTERNAL);
        accountRepository.save(virtualChecking);
        AccountHolder primaryOwner = (AccountHolder) userService.findByUsername(virtualAccountHolder.getUsername()).get();
        Long bankAccountId = accountService.findByPrimaryOwner(primaryOwner).get().getId();
        RegularTransaction transaction = new RegularTransaction();
        if (fromUserToBank) {
            transaction.setAmount((account.getBalance()).subtract(amount));
            transaction.setFromAccountId(account.getId());
            transaction.setToAccountId(bankAccountId);
            transaction.setFromUsername(account.getPrimaryOwner().getUsername());
            transaction.setToUsername(virtualAccountHolder.getUsername());
        } else {
            transaction.setAmount((amount).subtract(account.getBalance()));
            transaction.setFromAccountId(bankAccountId);
            transaction.setToAccountId(account.getId());
            transaction.setFromUsername(virtualAccountHolder.getUsername());
            transaction.setToUsername(account.getPrimaryOwner().getUsername());
        }
        transaction.setSignature("masterPassword");
        transaction.setConcept("Automatic generated Transaction");
        transactionService.executeTransaction(transaction);
        return "Balance of account " + account.getId() + " is now: " + account.getBalance() + '\n' +
                "Auto-generated transaction id is: " + transaction.getId() + '\n' +
                "Auto-generated account id is: " + virtualChecking.getId();
    }

    public String activate(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Account not found")
        );
        if (account instanceof Checking) {
            ((Checking) account).setAccountStatus(ACTIVE);}
        else if (account instanceof Savings) { ((Savings) account).setAccountStatus(ACTIVE); }
        else if (account instanceof StudentChecking) { ((StudentChecking) account).setAccountStatus(ACTIVE); }
        accountRepository.save(account);
        return "Account " + account.getId() + " is now active";
    }

    public String freeze(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Account not found")
        );
        if (account instanceof Checking) { ((Checking) account).setAccountStatus(FROZEN); }
        else if (account instanceof Savings) { ((Savings) account).setAccountStatus(FROZEN); }
        else if (account instanceof StudentChecking) { ((StudentChecking) account).setAccountStatus(FROZEN); }
        accountRepository.save(account);
        return "Account " + account.getId() + " is now frozen";
    }
}
