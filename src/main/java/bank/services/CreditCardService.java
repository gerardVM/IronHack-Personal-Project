package bank.services;

import bank.models.accounts.CreditCard;
import bank.models.roles.AccountHolder;
import bank.repositories.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreditCardService {
    @Autowired
    private CreditCardRepository creditCardRepository;

    public Optional<CreditCard> findByPrimaryOwner(AccountHolder user) {
        return creditCardRepository.findByPrimaryOwner(user);
    }
}
