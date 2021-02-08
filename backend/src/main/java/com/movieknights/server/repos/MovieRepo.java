package com.movieknights.server.repos;

import com.movieknights.server.entities.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepo extends Neo4jRepository<Movie, Long> {
}
