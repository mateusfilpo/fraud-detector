package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import java.time.LocalDateTime;

@Node("Account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountNode {

    @Id
    private String accountId;

    private String holder;
    private String type; // Enum armazenado como string
    private LocalDateTime createdAt;
    private String status; // Enum armazenado como string
}