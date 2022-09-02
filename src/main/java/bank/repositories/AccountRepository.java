package bank.repositories;

import bank.enums.Status;
import bank.models.User;
import bank.models.accounts.Account;
import bank.models.roles.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByPrimaryOwner(User user);
    List<Account> findAllBySecondaryOwner(User user);
    Optional<Account> findByPrimaryOwner(User user);
    Optional<Account> findBySecretKey(String hashedSecretKey);

}


