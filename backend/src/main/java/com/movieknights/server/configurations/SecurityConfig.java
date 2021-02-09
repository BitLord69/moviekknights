package com.movieknights.server.configurations;

import com.movieknights.server.jwt.AuthEntryPointJwt;
import com.movieknights.server.jwt.AuthTokenFilter;
import com.movieknights.server.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity()
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .cors().and()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
        .authorizeRequests()
        .antMatchers("/rest/calendar/freebusy").authenticated()
        .antMatchers("/rest/date").permitAll()
        .antMatchers(HttpMethod.POST,"/rest/calendar/add").authenticated()
        .antMatchers("/api/auth/whoami").permitAll()
        .antMatchers("/api/auth/storeauthcode").permitAll()
        .antMatchers("/api/auth/refreshtoken").permitAll()
        .antMatchers( "/").permitAll()
        .antMatchers(HttpMethod.GET, "/rest/movies/*").permitAll()
        .antMatchers(HttpMethod.GET, "/rest/movies/page/*").permitAll()
        .antMatchers(HttpMethod.GET, "/rest/person/director/*").permitAll()
        .antMatchers(HttpMethod.GET, "/rest/person/composer/*").permitAll()
        .antMatchers(HttpMethod.GET, "/rest/person/actor/*").permitAll()
        .anyRequest().authenticated();
    ;

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(userDetailsService.getEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

//  @Override
//  public void configure(WebSecurity web) throws Exception {
//    // TokenAuthenticationFilter will ignore the below paths
////    web.ignoring().antMatchers(
////        HttpMethod.POST,
////        "/api/auth/storeauthcode"
////    );
//////    web.ignoring().antMatchers(
//////        HttpMethod.GET,
//////        "/api/auth/whoami"
//////    );
//  }
}
