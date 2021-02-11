package com.movieknights.server.repos;

import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.relationships.HasActor;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;

@Repository
public interface PersonRepo extends Neo4jRepository<Person, Long> {

    @Query("MATCH (m:MKMovie)-[r]-(p:MKPerson) WHERE p.id = $id RETURN m, r, p")
    Collection<Movie> getAllMoviesPersonWorkedWith(long id);
}
