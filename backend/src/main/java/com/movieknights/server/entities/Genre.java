package com.movieknights.server.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Node("mkgenre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Genre {

    @Id
    private long id;

    private String name;
}
