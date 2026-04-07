package br.com.filpo.frauddetector.domain.ports.out;

import br.com.filpo.frauddetector.domain.models.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String transactionId);

    List<Transaction> findByAccountId(String accountId);
}