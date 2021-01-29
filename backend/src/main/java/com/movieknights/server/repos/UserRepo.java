package com.movieknights.server.repos;

import com.movieknights.server.entities.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.repository.Neo4jRepository;

@Repository
public interface UserRepo extends Neo4jRepository<User, String> {
}
