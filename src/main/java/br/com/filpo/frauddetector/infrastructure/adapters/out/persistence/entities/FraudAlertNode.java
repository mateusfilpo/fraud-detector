package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import java.time.LocalDateTime;
import java.util.List;

@Node("FraudAlert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAlertNode {

    @Id
    private String alertId;

    private String transactionId; // Referência por ID (sem @Relationship)
    private String accountId; // Referência por ID (sem @Relationship)
    private String pattern; // FraudPattern como string
    private String description;
    private int riskScore;
    private List<String> evidencePath; // Neo4j suporta listas nativamente
    private String status; // AlertStatus como string
    private LocalDateTime createdAt;
}