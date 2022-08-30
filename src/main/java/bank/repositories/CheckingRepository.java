package bank.repositories;

import bank.models.Checking;
import bank.models.roles.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckingRepository extends JpaRepository<Checking, Long> {

    Optional<Checking> findByPrimaryOwner(AccountHolder user);
}

