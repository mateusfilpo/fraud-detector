package br.com.filpo.frauddetector.integration;

import br.com.filpo.frauddetector.Neo4jIntegrationTest;
import br.com.filpo.frauddetector.domain.enums.AccountType;
import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import br.com.filpo.frauddetector.domain.models.*;
import br.com.filpo.frauddetector.domain.ports.in.AccountUseCase;
import br.com.filpo.frauddetector.domain.ports.in.FraudDetectionUseCase;
import br.com.filpo.frauddetector.domain.ports.in.TransactionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SharedDeviceDetectionIT extends Neo4jIntegrationTest {

    @Autowired
    private AccountUseCase accountUseCase;
    @Autowired
    private TransactionUseCase transactionUseCase;
    @Autowired
    private FraudDetectionUseCase fraudDetectionUseCase;

    @Test
    void shouldDetectSharedDevice() {
        // Given: 2 contas usam o mesmo device
        Account a = accountUseCase.createAccount("Device-A", AccountType.CHECKING);
        Account b = accountUseCase.createAccount("Device-B", AccountType.CHECKING);
        Account receiver = accountUseCase.createAccount("Receiver", AccountType.CHECKING);

        Device sharedDevice = new Device("SHARED-FP-TEST", "192.168.1.1", "Mozilla/5.0");

        transactionUseCase.createTransaction(new BigDecimal("1000"), TransactionChannel.WEB,
                a.getAccountId(), receiver.getAccountId(), sharedDevice, null, null);
        transactionUseCase.createTransaction(new BigDecimal("2000"), TransactionChannel.WEB,
                b.getAccountId(), receiver.getAccountId(), sharedDevice, null, null);

        // When
        List<FraudAlert> alerts = fraudDetectionUseCase.detectSharedDevice(a.getAccountId());

        // Then
        assertThat(alerts).isNotEmpty();
        assertThat(alerts).allMatch(alert -> alert.getPattern() == FraudPattern.SHARED_DEVICE);
    }

    @Test
    void shouldNotDetectSharedDeviceWithUniqueDevices() {
        // Given: cada conta usa device diferente
        Account a = accountUseCase.createAccount("Unique-A", AccountType.CHECKING);
        Account b = accountUseCase.createAccount("Unique-B", AccountType.CHECKING);

        transactionUseCase.createTransaction(new BigDecimal("1000"), TransactionChannel.WEB,
                a.getAccountId(), b.getAccountId(),
                new Device("FP-001", "10.0.0.1", "Chrome"), null, null);
        transactionUseCase.createTransaction(new BigDecimal("1000"), TransactionChannel.WEB,
                b.getAccountId(), a.getAccountId(),
                new Device("FP-002", "10.0.0.2", "Safari"), null, null);

        // When
        List<FraudAlert> alerts = fraudDetectionUseCase.detectSharedDevice(a.getAccountId());

        // Then
        assertThat(alerts).isEmpty();
    }
}
