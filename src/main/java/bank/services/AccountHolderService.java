package bank.services;

import bank.models.roles.AccountHolder;
import bank.repositories.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AccountHolderService {
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public Optional<AccountHolder> findByUsername(String username) {
        return accountHolderRepository.findByUsername(username);
    }
}
