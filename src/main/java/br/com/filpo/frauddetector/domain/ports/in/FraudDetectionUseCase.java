package br.com.filpo.frauddetector.domain.ports.in;

import br.com.filpo.frauddetector.domain.models.FraudAlert;
import br.com.filpo.frauddetector.domain.models.FraudScore;
import java.util.List;

public interface FraudDetectionUseCase {
    List<FraudAlert> analyzeTransaction(String transactionId);

    FraudScore calculateFraudScore(String transactionId);

    List<FraudAlert> detectFraudRings(String accountId);

    List<FraudAlert> detectSharedDevice(String accountId);

    List<FraudAlert> detectImpossibleTravel(String accountId);

    List<FraudAlert> detectSuspiciousMerchant(String merchantId);
}