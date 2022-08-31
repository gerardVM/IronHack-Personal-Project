package bank.repositories;

import bank.models.accounts.CreditCard;
import bank.models.roles.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    Optional<CreditCard> findByPrimaryOwner(AccountHolder user);
}

