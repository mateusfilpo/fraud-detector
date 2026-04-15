package br.com.filpo.frauddetector.integration;

import br.com.filpo.frauddetector.Neo4jIntegrationTest;
import br.com.filpo.frauddetector.domain.enums.AccountType;
import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import br.com.filpo.frauddetector.domain.models.Account;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import br.com.filpo.frauddetector.domain.ports.in.AccountUseCase;
import br.com.filpo.frauddetector.domain.ports.in.FraudDetectionUseCase;
import br.com.filpo.frauddetector.domain.ports.in.TransactionUseCase;
import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FraudRingDetectionIT extends Neo4jIntegrationTest {

    @Autowired
    private AccountUseCase accountUseCase;
    @Autowired
    private TransactionUseCase transactionUseCase;
    @Autowired
    private FraudDetectionUseCase fraudDetectionUseCase;

    @Test
    void shouldDetectFraudRingOfSize2() {
        // Given: A → B → A (ciclo de tamanho 2)
        Account a = accountUseCase.createAccount("Ring-A", AccountType.CHECKING);
        Account b = accountUseCase.createAccount("Ring-B", AccountType.CHECKING);

        transactionUseCase.createTransaction(new BigDecimal("5000"), TransactionChannel.WEB,
                a.getAccountId(), b.getAccountId(), null, null, null);
        transactionUseCase.createTransaction(new BigDecimal("4800"), TransactionChannel.WEB,
                b.getAccountId(), a.getAccountId(), null, null, null);

        // When
        List<FraudAlert> alerts = fraudDetectionUseCase.detectFraudRings(a.getAccountId());

        // Then
        assertThat(alerts).isNotEmpty();
        assertThat(alerts).allMatch(alert -> alert.getPattern() == FraudPattern.FRAUD_RING);
        assertThat(alerts.get(0).getEvidencePath())
                .contains(a.getAccountId(), b.getAccountId());
    }

    @Test
    void shouldDetectFraudRingOfSize3() {
        // Given: A → B → C → A (ciclo de tamanho 3)
        Account a = accountUseCase.createAccount("Ring3-A", AccountType.CHECKING);
        Account b = accountUseCase.createAccount("Ring3-B", AccountType.CHECKING);
        Account c = accountUseCase.createAccount("Ring3-C", AccountType.CHECKING);

        transactionUseCase.createTransaction(new BigDecimal("10000"), TransactionChannel.TRANSFER,
                a.getAccountId(), b.getAccountId(), null, null, null);
        transactionUseCase.createTransaction(new BigDecimal("9500"), TransactionChannel.TRANSFER,
                b.getAccountId(), c.getAccountId(), null, null, null);
        transactionUseCase.createTransaction(new BigDecimal("9000"), TransactionChannel.TRANSFER,
                c.getAccountId(), a.getAccountId(), null, null, null);

        // When
        List<FraudAlert> alerts = fraudDetectionUseCase.detectFraudRings(a.getAccountId());

        // Then
        assertThat(alerts).isNotEmpty();
        assertThat(alerts).allMatch(alert -> alert.getPattern() == FraudPattern.FRAUD_RING);
    }

    @Test
    void shouldNotDetectRingForLinearTransactions() {
        // Given: A → B → C (sem ciclo)
        Account a = accountUseCase.createAccount("Linear-A", AccountType.CHECKING);
        Account b = accountUseCase.createAccount("Linear-B", AccountType.CHECKING);
        Account c = accountUseCase.createAccount("Linear-C", AccountType.CHECKING);

        transactionUseCase.createTransaction(new BigDecimal("1000"), TransactionChannel.TRANSFER,
                a.getAccountId(), b.getAccountId(), null, null, null);
        transactionUseCase.createTransaction(new BigDecimal("1000"), TransactionChannel.TRANSFER,
                b.getAccountId(), c.getAccountId(), null, null, null);

        // When
        List<FraudAlert> alerts = fraudDetectionUseCase.detectFraudRings(a.getAccountId());

        // Then
        assertThat(alerts).isEmpty();
    }
}
