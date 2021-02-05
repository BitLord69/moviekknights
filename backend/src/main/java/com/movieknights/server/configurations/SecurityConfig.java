package com.movieknights.server.configurations;

import com.movieknights.server.services.UserDetailsServiceImpl;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories;
import org.springframework.data.neo4j.repository.config.ReactiveNeo4jRepositoryConfigurationExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableReactiveNeo4jRepositories
public class SecurityConfig {
  //  public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private UserDetailsServiceImpl userDetailsService;

//  @Autowired
//  private AuthEntryPointJwt unauthorizedHandler;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private SecurityContextRepository securityContextRepository;

  @Bean(ReactiveNeo4jRepositoryConfigurationExtension.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  public ReactiveTransactionManager reactiveTransactionManager(
      Driver driver,
      ReactiveDatabaseSelectionProvider databaseNameProvider) {
    return new ReactiveNeo4jTransactionManager(driver, databaseNameProvider);
  }


  @Bean
  public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
    return http
        .exceptionHandling()
        .authenticationEntryPoint((swe, e) -> {
          return Mono.fromRunnable(() -> {
            swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          });
        }).accessDeniedHandler((swe, e) -> {
          return Mono.fromRunnable(() -> {
            swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
          });
        }).and()
        .csrf().disable()
        .formLogin().disable()
        .httpBasic().disable()
        .authenticationManager(authenticationManager)
        .securityContextRepository(securityContextRepository)
        .authorizeExchange()
        .pathMatchers(HttpMethod.OPTIONS).permitAll()
        .pathMatchers( "/").permitAll()
        .pathMatchers("/api/auth/whoami").permitAll()
        .pathMatchers("/rest/calendar/freebusy").authenticated()
        .pathMatchers("/api/auth/storeauthcode").permitAll()
        .pathMatchers(HttpMethod.GET, "/rest/movies/*").permitAll()
        .anyExchange().authenticated()
        .and().build();
  }
}
//@Configuration
//@EnableWebSecurity()
