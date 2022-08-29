package bank.services;

import bank.models.Checking;
import bank.models.roles.AccountHolder;
import bank.models.roles.Admin;
import bank.repositories.CheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckingService {
    @Autowired
    private CheckingRepository checkingRepository;

    public Optional<Checking> findByPrimaryOwner(AccountHolder user) {
        return checkingRepository.findByPrimaryOwner(user);
    }
}
