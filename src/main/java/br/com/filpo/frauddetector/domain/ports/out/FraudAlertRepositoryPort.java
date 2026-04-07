package br.com.filpo.frauddetector.domain.ports.out;

import br.com.filpo.frauddetector.domain.enums.AlertStatus;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import java.util.List;
import java.util.Optional;

public interface FraudAlertRepositoryPort {
    FraudAlert save(FraudAlert alert);

    Optional<FraudAlert> findById(String alertId);

    List<FraudAlert> findAll();

    List<FraudAlert> findByStatus(AlertStatus status);

    List<FraudAlert> findByAccountId(String accountId);

    List<FraudAlert> findByTransactionId(String transactionId);
}