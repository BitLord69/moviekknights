package com.movieknights.server.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
