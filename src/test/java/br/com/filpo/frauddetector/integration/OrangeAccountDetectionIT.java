package br.com.filpo.frauddetector.integration;

import br.com.filpo.frauddetector.Neo4jIntegrationTest;
import br.com.filpo.frauddetector.domain.enums.AccountType;
import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import br.com.filpo.frauddetector.domain.models.Account;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import br.com.filpo.frauddetector.domain.models.Transaction;
import br.com.filpo.frauddetector.domain.ports.in.AccountUseCase;
import br.com.filpo.frauddetector.domain.ports.in.FraudDetectionUseCase;
import br.com.filpo.frauddetector.domain.ports.in.TransactionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrangeAccountDetectionIT extends Neo4jIntegrationTest {

    @Autowired
    private AccountUseCase accountUseCase;
    @Autowired
    private TransactionUseCase transactionUseCase;
    @Autowired
    private FraudDetectionUseCase fraudDetectionUseCase;

    @Test
    void shouldDetectOrangeAccountViaAnalyzeTransaction() {
        // Given: conta laranja recebe de 6 fontes, envia para 1 destino
        Account orange = accountUseCase.createAccount("Laranja", AccountType.CHECKING);
        Account target = accountUseCase.createAccount("Target", AccountType.CHECKING);

        // 6 fontes enviam para a conta laranja
        for (int i = 0; i < 6; i++) {
            Account source = accountUseCase.createAccount("Source-" + i, AccountType.CHECKING);
            transactionUseCase.createTransaction(new BigDecimal("1000"),
                    TransactionChannel.TRANSFER,
                    source.getAccountId(), orange.getAccountId(), null, null, null);
        }

        // Laranja repassa para 1 destino
        Transaction tx = transactionUseCase.createTransaction(new BigDecimal("5000"),
                TransactionChannel.TRANSFER,
                orange.getAccountId(), target.getAccountId(), null, null, null);

        // When: analyzeTransaction detecta orange account
        List<FraudAlert> alerts = fraudDetectionUseCase.analyzeTransaction(tx.getTransactionId());

        // Then
        assertThat(alerts.stream()
                .filter(a -> a.getPattern() == FraudPattern.ORANGE_ACCOUNT)
                .toList()).isNotEmpty();
    }
}
