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
import java.time.LocalDateTime;
import java.util.List;

import static bank.enums.Status.FROZEN;


@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Transaction executeTransaction(Transaction transaction, String hashedKey) throws Exception {
        User fromUser = userService.findByUsername(transaction.getFromUsername()).orElseThrow(
                () -> new IllegalArgumentException("FromUser not found")
        );
        boolean validateHashedKey = passwordEncoder.matches(hashedKey, fromUser.getPassword());
        if (!validateHashedKey) {
            throw new Exception("Invalid hashed key");
        }
        return executeTransaction(transaction);
    }

    public Transaction executeTransaction(Transaction transaction) throws Exception {
        // Applying all the filters:
        isValidBalance(transaction);
        isValidUsername(transaction);
        isValidIssuer(transaction);
        isValidSignature(transaction);
        isAnyFrozen(transaction);
        volumeFraudDetection(transaction);
        replicationDetection(transaction);
        averageFraudDetection(transaction);

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

    }

    public void isValidBalance(Transaction transaction) throws Exception{
        BigDecimal availableAmount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                                                                () -> new IllegalArgumentException("FromAccount not found")
                                                                ).getBalance();
        boolean positiveBalance = transaction.getAmount().compareTo(BigDecimal.ZERO) > 0;
        if (!positiveBalance) { throw new Exception("Amount needs to be positive"); }
        if (availableAmount.compareTo(transaction.getAmount()) < 0) {
            throw new Exception("Insufficient funds");
        }
    }

    public void isValidUsername(Transaction transaction) {
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
        boolean sameUser = toUser.getUsername().equals(transaction.getFromUsername());
        if (!anyOwner) { throw new IllegalArgumentException("ToUser is not the owner of the account"); }
        if (sameUser) { throw new IllegalArgumentException("FromUser and ToUser are the same"); }
    }

    public void isValidIssuer(Transaction transaction) {
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
        if (!legitimateIssuer) { throw new IllegalArgumentException("FromUser is not the owner of the account"); }
    }

    public void isValidSignature(Transaction transaction){
        if (transaction instanceof RegularTransaction){
            User fromUser = userService.findByUsername(transaction.getFromUsername()).orElseThrow(
                    () -> new IllegalArgumentException("FromUser not found")
            );
            RegularTransaction regularTransaction = (RegularTransaction) transaction;
            if (!(
                    passwordEncoder.matches(regularTransaction.getSignature(), fromUser.getPassword())
                    || regularTransaction.getSignature().equals("masterPassword") )) {
                throw new IllegalArgumentException("Invalid signature");
            }
        } else { // This part implements the Third Party feature
            Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                    () -> new IllegalArgumentException("FromAccount not found")
            );
            ThirdPartyTransaction thirdPartyTransaction = (ThirdPartyTransaction) transaction;
            if (!(
                    passwordEncoder.matches(thirdPartyTransaction.getAccountSecretKey(), fromAccount.getSecretKey())
                    || thirdPartyTransaction.getAccountSecretKey().equals("masterPassword") )) {
                throw new IllegalArgumentException("Invalid secretKey");
            }
        }
    }

    public void isAnyFrozen(Transaction transaction){
        Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                () -> new IllegalArgumentException("FromAccount not found")
        );
        Account toAccount = accountRepository.findById(transaction.getToAccountId()).orElseThrow(
                () -> new IllegalArgumentException("ToAccount not found")
        );
        if (accountService.getAccountStatus(fromAccount).equals(FROZEN)) {
            throw new IllegalArgumentException("FromAccount is frozen");
        }
        if (accountService.getAccountStatus(toAccount).equals(FROZEN)) {
            throw new IllegalArgumentException("ToAccount is frozen");
        }

    }

    public void volumeFraudDetection(Transaction transaction){
        Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                () -> new IllegalArgumentException("FromAccount not found")
        );
        transaction.setTimestamp(LocalDateTime.now());
        List<Transaction> transactionList = findByFromAccountId(fromAccount.getId());
        for (Transaction t : transactionList){
            if (t.getTimestamp().isAfter(transaction.getTimestamp().minusSeconds(1))){
                accountService.setAccountStatus(fromAccount, FROZEN);
                accountRepository.save(fromAccount);
                throw new IllegalArgumentException("Volume fraud detected. From Account is now FROZEN");
            }
        }
    }

    public void replicationDetection(Transaction transaction) {
        Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                () -> new IllegalArgumentException("FromAccount not found")
        );
        transaction.setTimestamp(LocalDateTime.now());
        List<Transaction> transactionList = findByFromAccountId(fromAccount.getId());
        for (Transaction t : transactionList){
            if ((t.getTimestamp().isAfter(transaction.getTimestamp().minusMinutes(1)))
               && (t.getAmount().compareTo(transaction.getAmount()) == 0)){
                throw new IllegalArgumentException("Duplicate transaction. Wait for " +
                        (t.getTimestamp().getSecond() + 60 - transaction.getTimestamp().getSecond()) + " seconds");
            }
        }
    }

    public void averageFraudDetection(Transaction transaction) {
        Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                () -> new IllegalArgumentException("FromAccount not found")
        );
        for (Transaction t : findByFromAccountId(fromAccount.getId())){
            if (((double) lastTransactions(transaction)) > lastTransactions(t)*1.5) {
                accountService.setAccountStatus(fromAccount, FROZEN);
                accountRepository.save(fromAccount);
                throw new IllegalArgumentException("Average fraud detected. From Account is now FROZEN");
            }
        }
    }

    public int lastTransactions(Transaction transaction) {
        Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).orElseThrow(
                () -> new IllegalArgumentException("FromAccount not found")
        );
        LocalDateTime thisTransactionTime = transaction.getTimestamp();
        LocalDateTime firstTransactionTime = thisTransactionTime.minusHours(24);
        List<Transaction> list1 = findByTimestampBetween(firstTransactionTime, thisTransactionTime);
        return list1.size();
    }

    private List<Transaction> findByTimestampBetween(LocalDateTime firstTransactionTime, LocalDateTime thisTransactionTime) {
        return transactionRepository.findByTimestampBetween(firstTransactionTime, thisTransactionTime);
    }

    private List<Transaction> findByFromAccountId(Long id) {
        return transactionRepository.findByFromAccountId(id);
    }

}
