package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.mappers;

import br.com.filpo.frauddetector.domain.enums.AlertStatus;
import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.FraudAlertNode;

public final class FraudAlertMapper {

    private FraudAlertMapper() {
    }

    public static FraudAlertNode toNode(FraudAlert alert) {
        return FraudAlertNode.builder()
                .alertId(alert.getAlertId())
                .transactionId(alert.getTransactionId())
                .accountId(alert.getAccountId())
                .pattern(alert.getPattern().name())
                .description(alert.getDescription())
                .riskScore(alert.getRiskScore())
                .evidencePath(alert.getEvidencePath())
                .status(alert.getStatus().name())
                .createdAt(alert.getCreatedAt())
                .build();
    }

    public static FraudAlert toDomain(FraudAlertNode node) {
        return new FraudAlert(
                node.getAlertId(),
                node.getTransactionId(),
                node.getAccountId(),
                FraudPattern.valueOf(node.getPattern()),
                node.getDescription(),
                node.getRiskScore(),
                node.getEvidencePath(),
                AlertStatus.valueOf(node.getStatus()),
                node.getCreatedAt());
    }
}