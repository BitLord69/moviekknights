package com.movieknights.server.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
@Node("mkmovie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Movie {

    @Id
    private long id;

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
    private List<Long> genres;
    private boolean adult;
}
