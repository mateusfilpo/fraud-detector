package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories;

import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.MerchantNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SpringDataMerchantRepository extends Neo4jRepository<MerchantNode, String> {
}