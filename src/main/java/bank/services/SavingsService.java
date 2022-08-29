package bank.services;

import bank.models.Checking;
import bank.models.Savings;
import bank.models.roles.AccountHolder;
import bank.repositories.SavingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SavingsService {
    @Autowired
    private SavingsRepository savingsRepository;

    public Optional<Savings> findByPrimaryOwner(AccountHolder user) {
        return savingsRepository.findByPrimaryOwner(user);
    }
}
