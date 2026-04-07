package br.com.filpo.frauddetector.domain.enums;

public enum FraudPattern {
    FRAUD_RING("Anel de fraude — dinheiro circula entre contas e volta à origem", 40),
    ORANGE_ACCOUNT("Conta laranja — recebe de muitas fontes e repassa para uma única", 35),
    SHARED_DEVICE("Dispositivo compartilhado — contas sem relação no mesmo device/IP", 25),
    IMPOSSIBLE_TRAVEL("Velocity check — transações em locais geograficamente impossíveis", 30),
    SUSPICIOUS_MERCHANT("Merchant suspeito — recebe de muitas contas novas", 20);

    private final String description;
    private final int scoreWeight;

    FraudPattern(String description, int scoreWeight) {
        this.description = description;
        this.scoreWeight = scoreWeight;
    }

    public String getDescription() {
        return description;
    }

    public int getScoreWeight() {
        return scoreWeight;
    }
}