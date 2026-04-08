package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories;

import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.AccountNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SpringDataAccountRepository extends Neo4jRepository<AccountNode, String> {
}