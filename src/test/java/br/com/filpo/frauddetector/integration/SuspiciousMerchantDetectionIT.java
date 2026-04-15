package br.com.filpo.frauddetector.integration;

import br.com.filpo.frauddetector.Neo4jIntegrationTest;
import br.com.filpo.frauddetector.domain.enums.AccountType;
import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import br.com.filpo.frauddetector.domain.models.Account;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import br.com.filpo.frauddetector.domain.models.Merchant;
import br.com.filpo.frauddetector.domain.ports.in.AccountUseCase;
import br.com.filpo.frauddetector.domain.ports.in.FraudDetectionUseCase;
import br.com.filpo.frauddetector.domain.ports.in.TransactionUseCase;
import br.com.filpo.frauddetector.domain.ports.out.MerchantRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SuspiciousMerchantDetectionIT extends Neo4jIntegrationTest {

    @Autowired
    private AccountUseCase accountUseCase;
    @Autowired
    private TransactionUseCase transactionUseCase;
    @Autowired
    private FraudDetectionUseCase fraudDetectionUseCase;
    @Autowired
    private MerchantRepositoryPort merchantRepositoryPort;

    @Test
    void shouldDetectSuspiciousMerchant() {
        // Given: merchant recebe de 4 contas novas (criadas hoje)
        Merchant merchant = merchantRepositoryPort.save(new Merchant("Suspect Shop", "crypto"));

        Account receiver = accountUseCase.createAccount("Receiver", AccountType.CHECKING);

        for (int i = 0; i < 4; i++) {
            Account newAccount = accountUseCase.createAccount("New-" + i, AccountType.CHECKING);
            transactionUseCase.createTransaction(new BigDecimal("200"),
                    TransactionChannel.WEB,
                    newAccount.getAccountId(), receiver.getAccountId(),
                    null, null, merchant.getMerchantId());
        }

        // When
        List<FraudAlert> alerts = fraudDetectionUseCase.detectSuspiciousMerchant(
                merchant.getMerchantId());

        // Then
        assertThat(alerts).isNotEmpty();
        assertThat(alerts).allMatch(a -> a.getPattern() == FraudPattern.SUSPICIOUS_MERCHANT);
    }
}
