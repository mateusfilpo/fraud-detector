package br.com.filpo.frauddetector.domain.ports.in;

import br.com.filpo.frauddetector.domain.enums.AlertStatus;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import java.util.List;

public interface AlertUseCase {
    List<FraudAlert> findAll();

    List<FraudAlert> findByStatus(AlertStatus status);

    List<FraudAlert> findByAccountId(String accountId);

    FraudAlert findById(String alertId);

    void markUnderReview(String alertId);

    void confirmFraud(String alertId);

    void dismiss(String alertId);
}