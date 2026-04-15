package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.adapters;

import br.com.filpo.frauddetector.domain.ports.out.FraudDetectionQueryPort;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FraudDetectionQueryAdapter implements FraudDetectionQueryPort {

    private final Neo4jClient neo4jClient;

    // ══════════════════════════════════════════════════
    // 1. FRAUD RINGS — Ciclos no grafo (tamanho 2 e 3)
    // ══════════════════════════════════════════════════

    @Override
    public List<RingEvidence> findFraudRings(String accountId) {
        List<RingEvidence> results = new ArrayList<>();
        results.addAll(findRingsOfSize2(accountId));
        results.addAll(findRingsOfSize3(accountId));
        return results;
    }

    private Collection<RingEvidence> findRingsOfSize2(String accountId) {
        return neo4jClient.query("""
                MATCH (a:Account {accountId: $accountId})-[:SENT]->(t1:Transaction)
                      -[:RECEIVED_BY]->(b:Account)-[:SENT]->(t2:Transaction)
                      -[:RECEIVED_BY]->(a)
                WHERE a <> b
                RETURN DISTINCT
                    [a.accountId, b.accountId] AS ring,
                    [t1.transactionId, t2.transactionId] AS transactions
                """)
                .bind(accountId).to("accountId")
                .fetchAs(RingEvidence.class)
                .mappedBy((ts, record) -> new RingEvidence(
                        record.get("ring").asList(Value::asString),
                        record.get("transactions").asList(Value::asString)))
                .all();
    }

    private Collection<RingEvidence> findRingsOfSize3(String accountId) {
        return neo4jClient.query("""
                MATCH (a:Account {accountId: $accountId})-[:SENT]->(t1:Transaction)
                      -[:RECEIVED_BY]->(b:Account)-[:SENT]->(t2:Transaction)
                      -[:RECEIVED_BY]->(c:Account)-[:SENT]->(t3:Transaction)
                      -[:RECEIVED_BY]->(a)
                WHERE a <> b AND b <> c AND a <> c
                RETURN DISTINCT
                    [a.accountId, b.accountId, c.accountId] AS ring,
                    [t1.transactionId, t2.transactionId, t3.transactionId] AS transactions
                """)
                .bind(accountId).to("accountId")
                .fetchAs(RingEvidence.class)
                .mappedBy((ts, record) -> new RingEvidence(
                        record.get("ring").asList(Value::asString),
                        record.get("transactions").asList(Value::asString)))
                .all();
    }

    // ══════════════════════════════════════════════════
    // 2. SHARED DEVICE — Mesma fingerprint, contas distintas
    // ══════════════════════════════════════════════════

    @Override
    public List<SharedDeviceEvidence> findSharedDeviceAccounts(String accountId) {
        Collection<SharedDeviceEvidence> results = neo4jClient.query("""
                MATCH (a:Account {accountId: $accountId})-[:SENT]->(:Transaction)
                      -[:MADE_FROM]->(d:Device)<-[:MADE_FROM]-(:Transaction)
                      <-[:SENT]-(other:Account)
                WHERE a <> other
                RETURN DISTINCT other.accountId AS otherAccountId,
                                d.fingerprint AS deviceFingerprint
                """)
                .bind(accountId).to("accountId")
                .fetchAs(SharedDeviceEvidence.class)
                .mappedBy((ts, record) -> new SharedDeviceEvidence(
                        record.get("otherAccountId").asString(),
                        record.get("deviceFingerprint").asString()))
                .all();
        return new ArrayList<>(results);
    }

    // ══════════════════════════════════════════════════
    // 3. IMPOSSIBLE TRAVEL — Distância geográfica vs tempo
    // ══════════════════════════════════════════════════

    @Override
    public List<ImpossibleTravelEvidence> findImpossibleTravelPairs(
            String accountId, int minutesThreshold, double distanceThresholdKm) {
        Collection<ImpossibleTravelEvidence> results = neo4jClient.query("""
                MATCH (a:Account {accountId: $accountId})-[:SENT]->(t1:Transaction)
                      -[:ORIGINATED_IN]->(l1:Location),
                      (a)-[:SENT]->(t2:Transaction)-[:ORIGINATED_IN]->(l2:Location)
                WHERE t1 <> t2
                  AND t1.timestamp < t2.timestamp
                  AND duration.between(t1.timestamp, t2.timestamp).minutes < $minutesThreshold
                  AND point.distance(
                        point({latitude: l1.latitude, longitude: l1.longitude}),
                        point({latitude: l2.latitude, longitude: l2.longitude})
                      ) > $distanceThresholdMeters
                RETURN t1.transactionId AS txId1, t2.transactionId AS txId2,
                       l1.city AS city1, l2.city AS city2,
                       point.distance(
                           point({latitude: l1.latitude, longitude: l1.longitude}),
                           point({latitude: l2.latitude, longitude: l2.longitude})
                       ) / 1000.0 AS distanceKm,
                       duration.between(t1.timestamp, t2.timestamp).minutes AS minutesBetween
                ORDER BY t1.timestamp DESC
                """)
                .bind(accountId).to("accountId")
                .bind(minutesThreshold).to("minutesThreshold")
                .bind(distanceThresholdKm * 1000).to("distanceThresholdMeters")
                .fetchAs(ImpossibleTravelEvidence.class)
                .mappedBy((ts, record) -> new ImpossibleTravelEvidence(
                        record.get("txId1").asString(),
                        record.get("txId2").asString(),
                        record.get("city1").asString(),
                        record.get("city2").asString(),
                        record.get("distanceKm").asDouble(),
                        record.get("minutesBetween").asLong()))
                .all();
        return new ArrayList<>(results);
    }

    // ══════════════════════════════════════════════════
    // 4. ORANGE ACCOUNT — fan-in alto, fan-out baixo
    // ══════════════════════════════════════════════════

    @Override
    public OrangeAccountEvidence findOrangeAccountEvidence(String accountId) {
        return neo4jClient.query("""
                MATCH (source:Account)-[:SENT]->(:Transaction)-[:RECEIVED_BY]->(a:Account {accountId: $accountId})
                WITH a, count(DISTINCT source) AS sourceCount
                OPTIONAL MATCH (a)-[:SENT]->(:Transaction)-[:RECEIVED_BY]->(target:Account)
                WITH a, sourceCount, count(DISTINCT target) AS targetCount
                RETURN a.accountId AS accountId, sourceCount, targetCount
                """)
                .bind(accountId).to("accountId")
                .fetchAs(OrangeAccountEvidence.class)
                .mappedBy((ts, record) -> new OrangeAccountEvidence(
                        record.get("accountId").asString(),
                        record.get("sourceCount").asLong(),
                        record.get("targetCount").asLong()))
                .one()
                .orElse(null);
    }

    // ══════════════════════════════════════════════════
    // 5. SUSPICIOUS MERCHANT — muitas contas novas
    // ══════════════════════════════════════════════════

    @Override
    public SuspiciousMerchantEvidence findSuspiciousMerchantActivity(
            String merchantId, int daysThreshold) {
        return neo4jClient.query("""
                MATCH (a:Account)-[:SENT]->(:Transaction)-[:PAID_TO]->(m:Merchant {merchantId: $merchantId})
                WHERE a.createdAt > localdatetime() - duration({days: $daysThreshold})
                WITH m, collect(DISTINCT a.accountId) AS newAccountIds
                WHERE size(newAccountIds) >= 3
                RETURN m.merchantId AS merchantId, newAccountIds
                """)
                .bind(merchantId).to("merchantId")
                .bind(daysThreshold).to("daysThreshold")
                .fetchAs(SuspiciousMerchantEvidence.class)
                .mappedBy((ts, record) -> new SuspiciousMerchantEvidence(
                        record.get("merchantId").asString(),
                        record.get("newAccountIds").asList(Value::asString)))
                .one()
                .orElse(null);
    }
}