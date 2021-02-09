package com.movieknights.server.relationships;


import com.movieknights.server.entities.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@AllArgsConstructor
@NoArgsConstructor
@Data
@RelationshipProperties
public class HasActor {
    @TargetNode
    private Person person;

    private String character;
    private int order;
}
