package com.movieknights.server.jwt;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private String username;
  private String googleAccessToken;

  public JwtResponse(String accessToken, String username, String googleAccessToken) {
    this.token = accessToken;
    this.username = username;
    this.googleAccessToken = googleAccessToken;
  }

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getGoogleAccessToken() {
    return googleAccessToken;
  }

  public void setGoogleAccessToken(String googleAccessToken) {
    this.googleAccessToken = googleAccessToken;
  }
}