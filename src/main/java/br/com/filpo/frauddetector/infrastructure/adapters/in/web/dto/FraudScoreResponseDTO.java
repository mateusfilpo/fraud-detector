package br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto;

import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import br.com.filpo.frauddetector.domain.models.FraudScore;
import java.util.List;

public record FraudScoreResponseDTO(
        String transactionId,
        int totalScore,
        List<FraudPattern> detectedPatterns,
        String riskLevel,
        boolean suspicious) {

    public static FraudScoreResponseDTO from(FraudScore score) {
        return new FraudScoreResponseDTO(
                score.transactionId(),
                score.totalScore(),
                score.detectedPatterns(),
                score.riskLevel(),
                score.isSuspicious());
    }
}