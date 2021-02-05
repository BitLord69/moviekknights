package com.movieknights.server.repos;

import com.movieknights.server.entities.Movie;
import org.neo4j.driver.internal.shaded.reactor.core.publisher.Mono;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepo extends ReactiveNeo4jRepository<Movie, Long> {
//    Mono<Movie> findMovieByMovieId(long id);
}
