package bank.repositories;

import bank.models.Transactions.RegularTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegularTransactionRepository extends JpaRepository<RegularTransaction, Long> {

}

