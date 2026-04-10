package br.com.filpo.frauddetector.application.services;

import br.com.filpo.frauddetector.domain.enums.FraudPattern;
import br.com.filpo.frauddetector.domain.exceptions.ResourceNotFoundException;
import br.com.filpo.frauddetector.domain.models.FraudAlert;
import br.com.filpo.frauddetector.domain.models.FraudScore;
import br.com.filpo.frauddetector.domain.models.Transaction;
import br.com.filpo.frauddetector.domain.ports.in.FraudDetectionUseCase;
import br.com.filpo.frauddetector.domain.ports.out.FraudAlertRepositoryPort;
import br.com.filpo.frauddetector.domain.ports.out.FraudDetectionQueryPort;
import br.com.filpo.frauddetector.domain.ports.out.FraudDetectionQueryPort.*;
import br.com.filpo.frauddetector.domain.ports.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FraudDetectionService implements FraudDetectionUseCase {

    private final FraudDetectionQueryPort queryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final FraudAlertRepositoryPort fraudAlertRepositoryPort;

    // ── Constantes de detecção ──
    private static final int IMPOSSIBLE_TRAVEL_MINUTES = 60;
    private static final double IMPOSSIBLE_TRAVEL_DISTANCE_KM = 500.0;
    private static final long ORANGE_ACCOUNT_MIN_SOURCES = 5;
    private static final long ORANGE_ACCOUNT_MAX_TARGETS = 2;
    private static final int SUSPICIOUS_MERCHANT_DAYS = 30;

    // ══════════════════════════════════════════
    // analyzeTransaction — executa TODOS os padrões
    // ══════════════════════════════════════════

    @Override
    @Transactional
    public List<FraudAlert> analyzeTransaction(String transactionId) {
        Transaction tx = transactionRepositoryPort.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));

        List<FraudAlert> alerts = new ArrayList<>();

        // 1. Fraud Rings
        alerts.addAll(detectFraudRingsInternal(tx.getSenderAccountId(), transactionId));

        // 2. Shared Device
        alerts.addAll(detectSharedDeviceInternal(tx.getSenderAccountId(), transactionId));

        // 3. Impossible Travel
        alerts.addAll(detectImpossibleTravelInternal(tx.getSenderAccountId(), transactionId));

        // 4. Orange Account (sem método público no port IN)
        alerts.addAll(detectOrangeAccountInternal(tx.getSenderAccountId(), transactionId));

        // 5. Suspicious Merchant
        if (tx.getMerchantId() != null) {
            alerts.addAll(detectSuspiciousMerchantInternal(tx.getMerchantId(),
                    tx.getSenderAccountId(), transactionId));
        }

        // Persistir todos os alertas
        return alerts.stream()
                .map(fraudAlertRepositoryPort::save)
                .toList();
    }

    // ══════════════════════════════════════════
    // calculateFraudScore
    // ══════════════════════════════════════════

    @Override
    public FraudScore calculateFraudScore(String transactionId) {
        List<FraudAlert> alerts = fraudAlertRepositoryPort.findByTransactionId(transactionId);
        List<FraudPattern> patterns = alerts.stream()
                .map(FraudAlert::getPattern)
                .distinct()
                .toList();
        return FraudScore.calculate(transactionId, patterns);
    }

    // ══════════════════════════════════════════
    // Métodos públicos do FraudDetectionUseCase
    // (executam detecção individual, sem transactionId)
    // ══════════════════════════════════════════

    @Override
    @Transactional
    public List<FraudAlert> detectFraudRings(String accountId) {
        List<FraudAlert> alerts = detectFraudRingsInternal(accountId, null);
        return alerts.stream()
                .map(fraudAlertRepositoryPort::save)
                .toList();
    }

    @Override
    @Transactional
    public List<FraudAlert> detectSharedDevice(String accountId) {
        List<FraudAlert> alerts = detectSharedDeviceInternal(accountId, null);
        return alerts.stream()
                .map(fraudAlertRepositoryPort::save)
                .toList();
    }

    @Override
    @Transactional
    public List<FraudAlert> detectImpossibleTravel(String accountId) {
        List<FraudAlert> alerts = detectImpossibleTravelInternal(accountId, null);
        return alerts.stream()
                .map(fraudAlertRepositoryPort::save)
                .toList();
    }

    @Override
    @Transactional
    public List<FraudAlert> detectSuspiciousMerchant(String merchantId) {
        List<FraudAlert> alerts = detectSuspiciousMerchantInternal(merchantId, null, null);
        return alerts.stream()
                .map(fraudAlertRepositoryPort::save)
                .toList();
    }

    // ══════════════════════════════════════════
    // Métodos internos de detecção
    // ══════════════════════════════════════════

    private List<FraudAlert> detectFraudRingsInternal(String accountId, String transactionId) {
        List<RingEvidence> rings = queryPort.findFraudRings(accountId);
        return rings.stream()
                .map(ring -> new FraudAlert(
                        transactionId,
                        accountId,
                        FraudPattern.FRAUD_RING,
                        FraudPattern.FRAUD_RING.getScoreWeight(),
                        ring.accountIds()))
                .toList();
    }

    private List<FraudAlert> detectSharedDeviceInternal(String accountId, String transactionId) {
        List<SharedDeviceEvidence> shared = queryPort.findSharedDeviceAccounts(accountId);
        if (shared.isEmpty()) {
            return List.of();
        }
        List<String> evidence = shared.stream()
                .map(sd -> sd.otherAccountId() + " via device " + sd.deviceFingerprint())
                .toList();
        return List.of(new FraudAlert(
                transactionId,
                accountId,
                FraudPattern.SHARED_DEVICE,
                FraudPattern.SHARED_DEVICE.getScoreWeight(),
                evidence));
    }

    private List<FraudAlert> detectImpossibleTravelInternal(String accountId, String transactionId) {
        List<ImpossibleTravelEvidence> pairs = queryPort.findImpossibleTravelPairs(
                accountId, IMPOSSIBLE_TRAVEL_MINUTES, IMPOSSIBLE_TRAVEL_DISTANCE_KM);
        return pairs.stream()
                .map(pair -> new FraudAlert(
                        transactionId,
                        accountId,
                        FraudPattern.IMPOSSIBLE_TRAVEL,
                        FraudPattern.IMPOSSIBLE_TRAVEL.getScoreWeight(),
                        List.of(
                                pair.txId1() + " em " + pair.city1(),
                                pair.txId2() + " em " + pair.city2(),
                                "%.0fkm em %dmin".formatted(pair.distanceKm(), pair.minutesBetween()))))
                .toList();
    }

    private List<FraudAlert> detectOrangeAccountInternal(String accountId, String transactionId) {
        OrangeAccountEvidence evidence = queryPort.findOrangeAccountEvidence(accountId);
        if (evidence == null
                || evidence.sourceCount() < ORANGE_ACCOUNT_MIN_SOURCES
                || evidence.targetCount() > ORANGE_ACCOUNT_MAX_TARGETS) {
            return List.of();
        }
        return List.of(new FraudAlert(
                transactionId,
                accountId,
                FraudPattern.ORANGE_ACCOUNT,
                FraudPattern.ORANGE_ACCOUNT.getScoreWeight(),
                List.of(
                        "Recebe de " + evidence.sourceCount() + " fontes",
                        "Envia para " + evidence.targetCount() + " destinos")));
    }

    private List<FraudAlert> detectSuspiciousMerchantInternal(
            String merchantId, String accountId, String transactionId) {
        SuspiciousMerchantEvidence evidence = queryPort.findSuspiciousMerchantActivity(
                merchantId, SUSPICIOUS_MERCHANT_DAYS);
        if (evidence == null || evidence.newAccountIds().isEmpty()) {
            return List.of();
        }
        return List.of(new FraudAlert(
                transactionId,
                accountId,
                FraudPattern.SUSPICIOUS_MERCHANT,
                FraudPattern.SUSPICIOUS_MERCHANT.getScoreWeight(),
                evidence.newAccountIds()));
    }
}