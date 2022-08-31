package bank.repositories;

import bank.models.accounts.StudentChecking;
import bank.models.roles.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentCheckingRepository extends JpaRepository<StudentChecking, Long> {

    Optional<StudentChecking> findByPrimaryOwner(AccountHolder user);
}
