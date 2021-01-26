package com.movieknights.server.repos;

import com.movieknights.server.entities.Genre;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepo extends Neo4jRepository<Genre, Long> {
}
