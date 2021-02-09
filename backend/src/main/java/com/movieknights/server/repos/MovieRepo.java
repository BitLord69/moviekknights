package com.movieknights.server.repos;

import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.relationships.HasActor;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Repository
public interface MovieRepo extends Neo4jRepository<Movie, Long> {
    List<Movie> findMoviesByTitleIsStartingWithIgnoreCase(String searchterm);

    @Query("MATCH (m:MKMovie)-[r:HAS_DIRECTOR]-(p:MKPerson{id: $id}) RETURN m ORDER BY m.releaseDate DESC")
    List<Movie> findAllMoviesFromDirector(long id);

    @Query("MATCH (m:MKMovie)-[r:HAS_COMPOSER]-(p:MKPerson{id: $id}) RETURN m ORDER BY m.releaseDate DESC")
    List<Movie> findAllMoviesFromComposer(long id);

    @Query("MATCH (m:MKMovie)-[r:HAS_ACTOR]-(p:MKPerson{id: $id}) RETURN m ORDER BY m.releaseDate DESC")
    List<Movie> findAllMoviesFromActor(long id);
}
