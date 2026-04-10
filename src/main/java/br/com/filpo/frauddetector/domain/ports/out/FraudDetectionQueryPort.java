package br.com.filpo.frauddetector.domain.ports.out;

import java.util.List;

/**
 * Port de saída para queries de detecção de fraude no grafo.
 * Cada método encapsula uma travessia Cypher específica e retorna evidência
 * bruta.
 */
public interface FraudDetectionQueryPort {

    // ── Records de evidência ──

    record RingEvidence(List<String> accountIds, List<String> transactionIds) {
    }

    record SharedDeviceEvidence(String otherAccountId, String deviceFingerprint) {
    }

    record ImpossibleTravelEvidence(
            String txId1, String txId2,
            String city1, String city2,
            double distanceKm, long minutesBetween) {
    }

    record OrangeAccountEvidence(String accountId, long sourceCount, long targetCount) {
    }

    record SuspiciousMerchantEvidence(String merchantId, List<String> newAccountIds) {
    }

    // ── Métodos de consulta ──

    /**
     * Encontra ciclos (A → B → ... → A) onde dinheiro circula e volta à origem.
     * Busca rings de tamanho 2 e 3.
     */
    List<RingEvidence> findFraudRings(String accountId);

    /**
     * Encontra contas que compartilham dispositivos (mesmo fingerprint) com a conta
     * dada.
     */
    List<SharedDeviceEvidence> findSharedDeviceAccounts(String accountId);

    /**
     * Encontra pares de transações da mesma conta em locais geograficamente
     * distantes
     * dentro de um intervalo de tempo impossível.
     */
    List<ImpossibleTravelEvidence> findImpossibleTravelPairs(String accountId,
            int minutesThreshold,
            double distanceThresholdKm);

    /**
     * Verifica se uma conta apresenta padrão laranja:
     * recebe de muitas fontes e repassa para poucos destinos.
     */
    OrangeAccountEvidence findOrangeAccountEvidence(String accountId);

    /**
     * Encontra contas recém-criadas (< daysThreshold dias) que transacionam com o
     * merchant.
     */
    SuspiciousMerchantEvidence findSuspiciousMerchantActivity(String merchantId,
            int daysThreshold);
}