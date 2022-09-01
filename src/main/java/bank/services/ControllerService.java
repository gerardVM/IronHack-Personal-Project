package bank.services;

import bank.models.User;
import bank.models.accounts.Account;
import bank.models.roles.AccountHolder;
// import bank.repositories.AccountHolderRepository;
import bank.repositories.AccountRepository;
import bank.repositories.UserRepository;
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
    // AccountHolderRepository accountHolderRepository;
    UserRepository userRepository;

    public List<String> findAllMyBalances(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("AccountHolder not found"));
        List<String> balances = accountRepository.findAllByPrimaryOwner((AccountHolder) user)
                                        .stream()
                                        .map(Account::getBalance)
                                        .map(BigDecimal::toString)
                                        .map(balance -> "Balance of account is: " + balance)
                                        .collect(Collectors.toList());
            return balances;

    }
}
