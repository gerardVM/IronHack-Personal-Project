package bank.repositories;

import bank.models.Checking;
import bank.models.roles.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckingRepository extends JpaRepository<Checking, Long> {

    Optional<Checking> findByPrimaryOwner(AccountHolder user);
}

