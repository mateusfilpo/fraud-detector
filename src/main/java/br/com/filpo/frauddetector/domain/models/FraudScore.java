package br.com.filpo.frauddetector.domain.models;

import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import java.util.List;

/**
 * Score composto de risco — cada padrão detectado adiciona pontos.
 */
public record FraudScore(
        String transactionId,
        int totalScore,
        List<FraudPattern> detectedPatterns,
        String riskLevel) {
    public FraudScore {
        if (totalScore < 0) {
            throw new IllegalArgumentException("Score não pode ser negativo");
        }
    }

    public static FraudScore calculate(String transactionId, List<FraudPattern> patterns) {
        int total = patterns.stream()
                .mapToInt(FraudPattern::getScoreWeight)
                .sum();
        total = Math.min(total, 100);

        String level;
        if (total >= 80) {
            level = "CRITICAL";
        } else if (total >= 50) {
            level = "HIGH";
        } else if (total >= 25) {
            level = "MEDIUM";
        } else {
            level = "LOW";
        }

        return new FraudScore(transactionId, total, patterns, level);
    }

    public boolean isSuspicious() {
        return totalScore >= 25;
    }
}