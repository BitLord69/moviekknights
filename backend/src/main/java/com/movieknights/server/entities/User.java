package com.movieknights.server.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@NoArgsConstructor
@Node("MKUser")
public class User {
  private static final long serialVersionUID = 1L;

  @Id
  String username;

  String name;

  @JsonIgnore
  String password;

  String pictureUrl;
  String googleAccessToken;
  String refreshToken;
  Long expiresAt;

  public User(String username, String name, String password, String pictureUrl, String googleAccessToken, String refreshToken, Long expiresAt) {
    this.username = username;
    this.name = name;
    this.password = password;
    this.pictureUrl = pictureUrl;
    this.googleAccessToken = googleAccessToken;
    this.refreshToken = refreshToken;
    this.expiresAt = expiresAt;
  }
}