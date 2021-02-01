package com.movieknights.server.entities;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Node("MKGenre")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Genre {
    @Id
    private long id;

    private String name;
}
