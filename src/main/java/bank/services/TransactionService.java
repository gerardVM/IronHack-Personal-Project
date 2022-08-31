package bank.services;

import bank.models.Account;
import bank.models.Transaction;
import bank.models.User;
import bank.repositories.AccountRepository;
import bank.repositories.TransactionRepository;
import bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Transaction executeTransaction(Transaction transaction){
        boolean isValidBalance = isValidBalance(transaction);
        boolean isValidUsername = isValidUsername(transaction);
        boolean isValidIssuer = isValidIssuer(transaction);
        boolean isValidSignature = isValidSignature(transaction);
        if ( isValidBalance && isValidUsername && isValidIssuer && isValidSignature){
            transaction.setSignature("accepted");
            Account from = accountRepository.findById(transaction.getFromAccountId()).get();
            Account to = accountRepository.findById(transaction.getToAccountId()).get();
            from.setBalance(from.getBalance().subtract(transaction.getAmount()));
            to.setBalance(to.getBalance().add(transaction.getAmount()));
            accountRepository.save(from);
            accountRepository.save(to);
            return transactionRepository.save(transaction);
        } else {
            throw new IllegalArgumentException("Invalid transaction");
        }
    }

    public boolean isValidBalance(Transaction transaction){
        BigDecimal availableAmount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                                                                () -> new IllegalArgumentException("FromAccount not found")
                                                                ).getBalance();
        return availableAmount.compareTo(transaction.getAmount()) >= 0;
    }

    public boolean isValidUsername(Transaction transaction){
        User toUser = userService.findByUsername(transaction.getToUsername()).orElseThrow(
                () -> new IllegalArgumentException("ToUser not found")
        );
        Account toAccount = accountRepository.findById(transaction.getToAccountId()).orElseThrow(
                () -> new IllegalArgumentException("ToAccount not found")
        );
        boolean primaryOrSecondaryOwner = toAccount.getPrimaryOwner().getUsername().equals(toUser.getUsername()) ||
                                        toAccount.getSecondaryOwner().getUsername().equals(toUser.getUsername());

        return primaryOrSecondaryOwner;
    }

    public boolean isValidIssuer(Transaction transaction) {
        User fromUser = userService.findByUsername(transaction.getFromUsername()).orElseThrow(
                () -> new IllegalArgumentException("FromUser not found")
        );
        Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                () -> new IllegalArgumentException("FromAccount not found")
        );
        boolean legitimateIssuer = fromAccount.getPrimaryOwner().getUsername().equals(fromUser.getUsername()) ||
                                    fromAccount.getSecondaryOwner().getUsername().equals(fromUser.getUsername());
        return legitimateIssuer;
    }

    public boolean isValidSignature(Transaction transaction){
        User fromUser = userService.findByUsername(transaction.getFromUsername()).orElseThrow(
                () -> new IllegalArgumentException("FromUser not found")
        );
        return passwordEncoder.matches(transaction.getSignature(), fromUser.getPassword());
    }


}
