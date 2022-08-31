package bank.repositories;

import bank.models.Transactions.ThirdPartyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyTransactionRepository extends JpaRepository<ThirdPartyTransaction, Long> {

}

