package com.movieknights.server.repos;

import com.movieknights.server.entities.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MovieRepo extends Neo4jRepository<Movie, Long> {
    List<Movie> findMoviesByTitleIsStartingWithIgnoreCase(String searchterm);

    @Query("MATCH (m:MKMovie)-[r]-(p:MKPerson) WHERE p.id = $id RETURN m, r , p")
    List<Movie> getAllMoviesPersonWorkedWith(long id);
}
