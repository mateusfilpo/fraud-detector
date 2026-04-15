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

class ImpossibleTravelDetectionIT extends Neo4jIntegrationTest {

    @Autowired
    private AccountUseCase accountUseCase;
    @Autowired
    private TransactionUseCase transactionUseCase;
    @Autowired
    private FraudDetectionUseCase fraudDetectionUseCase;

    @Test
    void shouldDetectImpossibleTravel() {
        // Given: mesma conta, São Paulo → Tokyo em minutos
        Account traveler = accountUseCase.createAccount("Traveler", AccountType.CHECKING);
        Account receiver = accountUseCase.createAccount("Receiver", AccountType.CHECKING);

        Location saoPaulo = new Location("São Paulo", "BR", -23.5505, -46.6333);
        Location tokyo = new Location("Tokyo", "JP", 35.6762, 139.6503);

        transactionUseCase.createTransaction(new BigDecimal("500"), TransactionChannel.POS,
                traveler.getAccountId(), receiver.getAccountId(),
                new Device("travel-fp", "10.0.0.1", "Chrome"), saoPaulo, null);
        transactionUseCase.createTransaction(new BigDecimal("800"), TransactionChannel.POS,
                traveler.getAccountId(), receiver.getAccountId(),
                new Device("travel-fp", "10.0.0.2", "Chrome"), tokyo, null);

        // When
        List<FraudAlert> alerts = fraudDetectionUseCase.detectImpossibleTravel(
                traveler.getAccountId());

        // Then
        assertThat(alerts).isNotEmpty();
        assertThat(alerts).allMatch(alert -> alert.getPattern() == FraudPattern.IMPOSSIBLE_TRAVEL);
    }

    @Test
    void shouldNotDetectTravelBetweenNearbyCities() {
        // Given: mesma conta, São Paulo → Campinas (~100km) — possível
        Account traveler = accountUseCase.createAccount("Local", AccountType.CHECKING);
        Account receiver = accountUseCase.createAccount("Receiver", AccountType.CHECKING);

        Location saoPaulo = new Location("São Paulo", "BR", -23.5505, -46.6333);
        Location campinas = new Location("Campinas", "BR", -22.9056, -47.0608);

        transactionUseCase.createTransaction(new BigDecimal("500"), TransactionChannel.POS,
                traveler.getAccountId(), receiver.getAccountId(),
                new Device("local-fp", "10.0.0.1", "Chrome"), saoPaulo, null);
        transactionUseCase.createTransaction(new BigDecimal("800"), TransactionChannel.POS,
                traveler.getAccountId(), receiver.getAccountId(),
                new Device("local-fp", "10.0.0.2", "Chrome"), campinas, null);

        // When
        List<FraudAlert> alerts = fraudDetectionUseCase.detectImpossibleTravel(
                traveler.getAccountId());

        // Then: ~100km < 500km threshold → não é impossible travel
        assertThat(alerts).isEmpty();
    }
}
