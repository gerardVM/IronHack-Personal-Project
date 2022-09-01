package bank.repositories;

import bank.models.Transactions.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountId(Long id);

    List<Transaction> findByTimestampBetween(LocalDateTime firstTransactionTime, LocalDateTime thisTransactionTime);
}

