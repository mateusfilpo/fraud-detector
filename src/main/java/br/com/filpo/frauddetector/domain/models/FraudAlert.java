package br.com.filpo.frauddetector.domain.models;

import br.com.filpo.frauddetector.domain.enums.AlertStatus;
import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FraudAlert {

    private final String alertId;
    private String transactionId;
    private String accountId;
    private FraudPattern pattern;
    private String description;
    private int riskScore;
    private List<String> evidencePath;
    private AlertStatus status;
    private LocalDateTime createdAt;

    public FraudAlert(String transactionId, String accountId,
            FraudPattern pattern, int riskScore, List<String> evidencePath) {
        this.alertId = UUID.randomUUID().toString();
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.pattern = pattern;
        this.description = pattern.getDescription();
        this.riskScore = riskScore;
        this.evidencePath = evidencePath;
        this.status = AlertStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public FraudAlert(String alertId, String transactionId, String accountId,
            FraudPattern pattern, String description, int riskScore,
            List<String> evidencePath, AlertStatus status,
            LocalDateTime createdAt) {
        this.alertId = alertId;
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.pattern = pattern;
        this.description = description;
        this.riskScore = riskScore;
        this.evidencePath = evidencePath;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void markUnderReview() {
        if (this.status != AlertStatus.OPEN) {
            throw new IllegalStateException("Apenas alertas abertos podem entrar em revisão");
        }
        this.status = AlertStatus.UNDER_REVIEW;
    }

    public void confirmFraud() {
        this.status = AlertStatus.CONFIRMED_FRAUD;
    }

    public void dismiss() {
        this.status = AlertStatus.DISMISSED;
    }

    public boolean isCritical() {
        return this.riskScore >= 80;
    }

    public String getAlertId() {
        return alertId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public FraudPattern getPattern() {
        return pattern;
    }

    public String getDescription() {
        return description;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public List<String> getEvidencePath() {
        return evidencePath;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FraudAlert that = (FraudAlert) o;
        return Objects.equals(alertId, that.alertId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId);
    }

    @Override
    public String toString() {
        return "FraudAlert{alertId='%s', pattern=%s, riskScore=%d, status=%s}"
                .formatted(alertId, pattern, riskScore, status);
    }
}