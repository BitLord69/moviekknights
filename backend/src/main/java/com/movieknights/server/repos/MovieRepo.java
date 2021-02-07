package com.movieknights.server.repos;

import com.movieknights.server.entities.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepo extends Neo4jRepository<Movie, Long> {
    Optional<Movie> findMovieByMovieId(long id);

    @Query("MATCH (m:MKMovie)-[r]-(p:MKPerson) WHERE p.id = $id RETURN m, r , p")
    List<Movie> getAllMoviesPersonWorkedWith(long id);
}
