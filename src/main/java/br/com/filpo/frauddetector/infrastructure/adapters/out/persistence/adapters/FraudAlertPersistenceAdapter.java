package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.adapters;

import br.com.filpo.frauddetector.domain.enums.AlertStatus;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import br.com.filpo.frauddetector.domain.ports.out.FraudAlertRepositoryPort;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.mappers.FraudAlertMapper;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories.SpringDataFraudAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FraudAlertPersistenceAdapter implements FraudAlertRepositoryPort {

    private final SpringDataFraudAlertRepository repository;

    @Override
    public FraudAlert save(FraudAlert alert) {
        var node = FraudAlertMapper.toNode(alert);
        var saved = repository.save(node);
        return FraudAlertMapper.toDomain(saved);
    }

    @Override
    public Optional<FraudAlert> findById(String alertId) {
        return repository.findById(alertId)
                .map(FraudAlertMapper::toDomain);
    }

    @Override
    public List<FraudAlert> findAll() {
        return repository.findAll().stream()
                .map(FraudAlertMapper::toDomain)
                .toList();
    }

    @Override
    public List<FraudAlert> findByStatus(AlertStatus status) {
        return repository.findByStatus(status.name()).stream()
                .map(FraudAlertMapper::toDomain)
                .toList();
    }

    @Override
    public List<FraudAlert> findByAccountId(String accountId) {
        return repository.findByAccountId(accountId).stream()
                .map(FraudAlertMapper::toDomain)
                .toList();
    }

    @Override
    public List<FraudAlert> findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId).stream()
                .map(FraudAlertMapper::toDomain)
                .toList();
    }
}