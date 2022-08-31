package bank.services;

import bank.models.Account;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountHolderRepository;
import bank.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ControllerService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountHolderRepository accountHolderRepository;

    public List<String> findAllMyBalances(String username) {
        AccountHolder accountHolder = accountHolderRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("AccountHolder not found"));
        List<String> balances = accountRepository.findAllByPrimaryOwner(accountHolder)
                                        .stream()
                                        .map(Account::getBalance)
                                        .map(BigDecimal::toString)
                                        .collect(Collectors.toList());
            return balances;

    }
}
