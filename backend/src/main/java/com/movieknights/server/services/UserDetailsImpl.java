package com.movieknights.server.services;

import java.util.*;

import com.movieknights.server.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserDetailsImpl extends BaseDetailsImpl {
  private static final long serialVersionUID = 1L;

  private final String email;
  private final String name;

  private final String pictureUrl;

  @JsonIgnore
  private final String password;

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public Long getExpiresAt() {
    return expiresAt;
  }

  private final Set<GrantedAuthority> authorities;

  private final String accessToken;
  private final String refreshToken;
  private final Long expiresAt;

  public UserDetailsImpl(String email, String name, String password, String pictureUrl, String accessToken, String refreshToken, Long expiresAt) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.pictureUrl = pictureUrl;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresAt = expiresAt;

    authorities = new HashSet<>();
    authorities.add(new SimpleGrantedAuthority("USER"));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public static UserDetailsImpl build(User user) {
    return new UserDetailsImpl(
        user.getUsername(),
        user.getName(),
        user.getPassword(),
        user.getPictureUrl(),
        user.getGoogleAccessToken(),
        user.getRefreshToken(),
        user.getExpiresAt());
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(email, user.email);
  }
}
