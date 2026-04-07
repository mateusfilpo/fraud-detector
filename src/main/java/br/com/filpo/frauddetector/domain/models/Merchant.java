package br.com.filpo.frauddetector.domain.models;

import java.util.Objects;
import java.util.UUID;

public class Merchant {

    private final String merchantId;
    private String name;
    private String category;
    private double riskScore;

    public Merchant(String name, String category) {
        this.merchantId = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.riskScore = 0.0;
    }

    public Merchant(String merchantId, String name, String category, double riskScore) {
        this.merchantId = merchantId;
        this.name = name;
        this.category = category;
        this.riskScore = riskScore;
    }

    public void updateRiskScore(double newScore) {
        if (newScore < 0 || newScore > 100) {
            throw new IllegalArgumentException("Risk score deve estar entre 0 e 100");
        }
        this.riskScore = newScore;
    }

    public boolean isHighRisk() {
        return this.riskScore >= 70.0;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getRiskScore() {
        return riskScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Merchant merchant = (Merchant) o;
        return Objects.equals(merchantId, merchant.merchantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(merchantId);
    }

    @Override
    public String toString() {
        return "Merchant{merchantId='%s', name='%s', category='%s', riskScore=%.1f}"
                .formatted(merchantId, name, category, riskScore);
    }
}