package bank.services;

import bank.enums.Status;
import bank.models.User;
import bank.models.accounts.Account;
import bank.models.accounts.Checking;
import bank.models.accounts.Savings;
import bank.models.accounts.StudentChecking;
import bank.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> findByPrimaryOwner(User user) {
        return accountRepository.findByPrimaryOwner(user);
    }

    public void setAccountStatus(Account account, Status status) {
        if (Checking.class.isInstance(account)) {
            ((Checking) account).setAccountStatus(status);
        } else if (Savings.class.isInstance(account)) {
            ((Savings) account).setAccountStatus(status);
        } else if (StudentChecking.class.isInstance(account)) {
            ((StudentChecking) account).setAccountStatus(status);
        } else {
            throw new IllegalArgumentException("Account type not supported");
        }
    }

    public Status getAccountStatus(Account account) {
        if (Checking.class.isInstance(account)) {
            return ((Checking) account).getAccountStatus();
        } else if (Savings.class.isInstance(account)) {
            return ((Savings) account).getAccountStatus();
        } else if (StudentChecking.class.isInstance(account)) {
            return ((StudentChecking) account).getAccountStatus();
        } else {
            throw new IllegalArgumentException("Account type not supported");
        }
    }
}
