package com.movieknights.server.configurations;

import com.movieknights.server.jwt.JwtUtils;
import com.movieknights.server.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Override
  @SuppressWarnings("unchecked")
  public Mono<Authentication> authenticate(Authentication authentication) {
    String authToken = authentication.getCredentials().toString();

    try {
      if (jwtUtils.validateJwtToken(authToken)) {
        String email = jwtUtils.getUserNameFromJwtToken(authToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        System.out.println("------------------ static AuthenticationManager.authenticate - authorities: " + userDetails.getAuthorities());

        return Mono.just(new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()));
      } else {
        return Mono.empty();
      }
    } catch (Exception e) {
      return Mono.empty();
    }
//      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
//
//
//    try {
//      if (!jwtUtil.validateToken(authToken)) {
//        return Mono.empty();
//      }
//      Claims claims = jwtUtil.getAllClaimsFromToken(authToken);
//      List<String> rolesMap = claims.get("role", List.class);
//      List<GrantedAuthority> authorities = new ArrayList<>();
//      for (String rolemap : rolesMap) {
//        authorities.add(new SimpleGrantedAuthority(rolemap));
//      }
//      return Mono.just(new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities));
//    } catch (Exception e) {
//      return Mono.empty();
//    }
//  }
}