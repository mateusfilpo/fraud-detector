package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories;

import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.FraudAlertNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.List;

public interface SpringDataFraudAlertRepository extends Neo4jRepository<FraudAlertNode, String> {

    List<FraudAlertNode> findByStatus(String status);

    List<FraudAlertNode> findByAccountId(String accountId);

    List<FraudAlertNode> findByTransactionId(String transactionId);
}