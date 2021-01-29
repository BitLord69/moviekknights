package com.movieknights.server.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@Service
public abstract class BaseDetailsImpl implements UserDetails {
  protected final static String REMOTE_URL = "https://pokeapi.co/api/v2/";
  protected final RestTemplate restTemplate = new RestTemplate();

//  @Override
//  public Collection<? extends GrantedAuthority> getAuthorities() {
//    return null;
//  }
}
