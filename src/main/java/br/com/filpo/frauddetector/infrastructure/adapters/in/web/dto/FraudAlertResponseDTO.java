package br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto;

import br.com.filpo.frauddetector.domain.enums.AlertStatus;
import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import java.time.LocalDateTime;
import java.util.List;

public record FraudAlertResponseDTO(
        String alertId,
        String transactionId,
        String accountId,
        FraudPattern pattern,
        String description,
        int riskScore,
        List<String> evidencePath,
        AlertStatus status,
        LocalDateTime createdAt) {

    public static FraudAlertResponseDTO from(FraudAlert alert) {
        return new FraudAlertResponseDTO(
                alert.getAlertId(),
                alert.getTransactionId(),
                alert.getAccountId(),
                alert.getPattern(),
                alert.getDescription(),
                alert.getRiskScore(),
                alert.getEvidencePath(),
                alert.getStatus(),
                alert.getCreatedAt());
    }
}