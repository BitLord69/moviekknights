package com.movieknights.server.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public abstract class BaseDetailsImpl implements UserDetails {
  protected final RestTemplate restTemplate = new RestTemplate();
}
