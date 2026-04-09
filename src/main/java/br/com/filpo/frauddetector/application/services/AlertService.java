package br.com.filpo.frauddetector.application.services;

import br.com.filpo.frauddetector.domain.enums.AlertStatus;
import br.com.filpo.frauddetector.domain.exceptions.ResourceNotFoundException;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import br.com.filpo.frauddetector.domain.ports.in.AlertUseCase;
import br.com.filpo.frauddetector.domain.ports.out.FraudAlertRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService implements AlertUseCase {

    private final FraudAlertRepositoryPort fraudAlertRepositoryPort;

    @Override
    public List<FraudAlert> findAll() {
        return fraudAlertRepositoryPort.findAll();
    }

    @Override
    public List<FraudAlert> findByStatus(AlertStatus status) {
        return fraudAlertRepositoryPort.findByStatus(status);
    }

    @Override
    public List<FraudAlert> findByAccountId(String accountId) {
        return fraudAlertRepositoryPort.findByAccountId(accountId);
    }

    @Override
    public FraudAlert findById(String alertId) {
        return fraudAlertRepositoryPort.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("FraudAlert", alertId));
    }

    @Override
    @Transactional
    public void markUnderReview(String alertId) {
        FraudAlert alert = findById(alertId);
        alert.markUnderReview();
        fraudAlertRepositoryPort.save(alert);
    }

    @Override
    @Transactional
    public void confirmFraud(String alertId) {
        FraudAlert alert = findById(alertId);
        alert.confirmFraud();
        fraudAlertRepositoryPort.save(alert);
    }

    @Override
    @Transactional
    public void dismiss(String alertId) {
        FraudAlert alert = findById(alertId);
        alert.dismiss();
        fraudAlertRepositoryPort.save(alert);
    }
}