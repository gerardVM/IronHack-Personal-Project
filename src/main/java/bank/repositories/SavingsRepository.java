package bank.repositories;

import bank.models.Savings;
import bank.models.roles.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingsRepository extends JpaRepository<Savings, Long> {

    Optional<Savings> findByPrimaryOwner(AccountHolder user);
}

