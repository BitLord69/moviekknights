package com.movieknights.server.repos;


import com.movieknights.server.entities.DBSetting;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DBSettingRepo extends Neo4jRepository<DBSetting, LocalDate> {
}
