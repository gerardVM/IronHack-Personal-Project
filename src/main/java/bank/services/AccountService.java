package bank.services;

import bank.enums.Status;
import bank.models.User;
import bank.models.accounts.Account;
import bank.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSession;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> findByPrimaryOwner(User user) {
        return accountRepository.findByPrimaryOwner(user);
    }

}
