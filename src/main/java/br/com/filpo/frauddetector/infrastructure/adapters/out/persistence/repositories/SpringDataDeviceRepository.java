package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories;

import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.DeviceNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.Optional;

public interface SpringDataDeviceRepository extends Neo4jRepository<DeviceNode, String> {
    Optional<DeviceNode> findByFingerprint(String fingerprint);
}