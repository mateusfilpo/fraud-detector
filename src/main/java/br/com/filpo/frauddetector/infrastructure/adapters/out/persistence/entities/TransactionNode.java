package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Node("Transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionNode {

    @Id
    private String transactionId;

    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String channel; // Enum como string

    // ── Relacionamentos (TransactionNode é o hub central) ──

    @Relationship(type = "SENT", direction = Relationship.Direction.INCOMING)
    private AccountNode sender;

    @Relationship(type = "RECEIVED_BY", direction = Relationship.Direction.OUTGOING)
    private AccountNode receiver;

    @Relationship(type = "MADE_FROM", direction = Relationship.Direction.OUTGOING)
    private DeviceNode device;

    @Relationship(type = "ORIGINATED_IN", direction = Relationship.Direction.OUTGOING)
    private LocationNode location;

    @Relationship(type = "PAID_TO", direction = Relationship.Direction.OUTGOING)
    private MerchantNode merchant;
}