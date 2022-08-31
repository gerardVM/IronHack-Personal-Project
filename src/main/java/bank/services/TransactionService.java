package bank.services;

import bank.models.Transactions.RegularTransaction;
import bank.models.Transactions.ThirdPartyTransaction;
import bank.models.accounts.Account;
import bank.models.Transactions.Transaction;
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
            // Keys needs to be removed from the transaction object before saving it to the database
            if (transaction instanceof RegularTransaction) {
                ((RegularTransaction) transaction).setSignature("ACCEPTED");
            } else { ((ThirdPartyTransaction) transaction).setAccountSecretKey("ACCEPTED"); }

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
        boolean anyOwner = (toAccount.getPrimaryOwner() != null && toAccount.getPrimaryOwner().getUsername().equals(toUser.getUsername()))
                        || (toAccount.getSecondaryOwner() != null && toAccount.getSecondaryOwner().getUsername().equals(toUser.getUsername()))
                        // Implementing the Third Party feature
                        || toAccount.getThirdParty().getUsername().equals(toUser.getUsername());

        return anyOwner;
    }

    public boolean isValidIssuer(Transaction transaction) {
        User fromUser = userService.findByUsername(transaction.getFromUsername()).orElseThrow(
                () -> new IllegalArgumentException("FromUser not found")
        );
        Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                () -> new IllegalArgumentException("FromAccount not found")
        );
        boolean legitimateIssuer = (fromAccount.getPrimaryOwner() != null && fromAccount.getPrimaryOwner().getUsername().equals(fromUser.getUsername()))
                                || (fromAccount.getSecondaryOwner() != null && fromAccount.getSecondaryOwner().getUsername().equals(fromUser.getUsername()))
                                // Implementing the Third Party feature
                                || fromAccount.getThirdParty().getUsername().equals(fromUser.getUsername());
        return legitimateIssuer;
    }

    public boolean isValidSignature(Transaction transaction){
        if (transaction instanceof RegularTransaction){
            User fromUser = userService.findByUsername(transaction.getFromUsername()).orElseThrow(
                    () -> new IllegalArgumentException("FromUser not found")
            );
            RegularTransaction regularTransaction = (RegularTransaction) transaction;
            return passwordEncoder.matches(regularTransaction.getSignature(), fromUser.getPassword());
        } else { // This part implements the Third Party feature
            Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                    () -> new IllegalArgumentException("FromAccount not found")
            );
            ThirdPartyTransaction thirdPartyTransaction = (ThirdPartyTransaction) transaction;

            return passwordEncoder.matches(thirdPartyTransaction.getAccountSecretKey(), fromAccount.getSecretKey());
        }
    }


}
