package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories;

import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.TransactionNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;
import java.util.Optional;

public interface SpringDataTransactionRepository extends Neo4jRepository<TransactionNode, String> {

    @Query("""
        MATCH (sender:Account)-[s:SENT]->(t:Transaction {transactionId: $transactionId})
        OPTIONAL MATCH (t)-[rb:RECEIVED_BY]->(receiver:Account)
        OPTIONAL MATCH (t)-[mf:MADE_FROM]->(d:Device)
        OPTIONAL MATCH (t)-[oi:ORIGINATED_IN]->(l:Location)
        OPTIONAL MATCH (t)-[pt:PAID_TO]->(m:Merchant)
        RETURN t, s, sender, rb, receiver, mf, d, oi, l, pt, m
    """)
    Optional<TransactionNode> findWithRelationshipsById(String transactionId);

    @Query("""
        MATCH (a:Account {accountId: $accountId})-[s:SENT]->(t:Transaction)
        OPTIONAL MATCH (t)-[rb:RECEIVED_BY]->(receiver:Account)
        OPTIONAL MATCH (t)-[mf:MADE_FROM]->(d:Device)
        OPTIONAL MATCH (t)-[oi:ORIGINATED_IN]->(l:Location)
        OPTIONAL MATCH (t)-[pt:PAID_TO]->(m:Merchant)
        RETURN t, s, a, rb, receiver, mf, d, oi, l, pt, m
    """)
    List<TransactionNode> findBySenderAccountId(String accountId);

    @Query("""
        MATCH (t:Transaction)-[rb:RECEIVED_BY]->(a:Account {accountId: $accountId})
        OPTIONAL MATCH (sender:Account)-[s:SENT]->(t)
        OPTIONAL MATCH (t)-[mf:MADE_FROM]->(d:Device)
        OPTIONAL MATCH (t)-[oi:ORIGINATED_IN]->(l:Location)
        OPTIONAL MATCH (t)-[pt:PAID_TO]->(m:Merchant)
        RETURN t, s, sender, rb, a, mf, d, oi, l, pt, m
    """)
    List<TransactionNode> findByReceiverAccountId(String accountId);
}