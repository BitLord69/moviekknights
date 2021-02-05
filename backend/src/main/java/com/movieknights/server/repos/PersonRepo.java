package com.movieknights.server.repos;

import com.movieknights.server.entities.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepo extends Neo4jRepository<Person, Long> {

    @Query("MATCH (p:MKPerson{id: $id})-[r]-(m:MKMovie) RETURN p{.*, movies:collect(m)}")
    Person getAllMoviesPersonWorkedWith(long id);
}
