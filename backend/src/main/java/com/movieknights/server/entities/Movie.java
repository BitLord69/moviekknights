package com.movieknights.server.entities;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import com.movieknights.server.relationships.HasActor;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Date;
import java.util.HashSet;

@Data
@Node("MKMovie")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Movie {
    @Id
    private long movieId;

    private String title;
    private String originalTitle;
    private String originalLanguage;
    private String tagline;
    private String overview;
    private String imdbId;
    private String status;
    private String posterPath;
    private String backdropPath;
    private Date releaseDate;
    private int runTime;
    private double popularity;
    @Relationship(type = "BELONGS_TO_GENRE", direction = Relationship.Direction.OUTGOING)
    private HashSet<Genre> genres;
    @Relationship(type = "HAS_DIRECTOR", direction = Relationship.Direction.OUTGOING)
    private HashSet<Person> directors;
    @Relationship(type = "HAS_ACTOR", direction = Relationship.Direction.OUTGOING)
    private HashSet<HasActor> cast;
    @Relationship(type = "HAS_COMPOSER", direction = Relationship.Direction.OUTGOING)
    private HashSet<Person> composers;
    private boolean adult;
}
