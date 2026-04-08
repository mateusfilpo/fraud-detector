package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationNode {

    @Id
    @GeneratedValue
    private Long id;

    private String city;
    private String country;
    private double latitude;
    private double longitude;
}