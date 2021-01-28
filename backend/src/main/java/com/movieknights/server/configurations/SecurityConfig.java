package com.movieknights.server.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity()
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//  @Autowired
//  private MyUserDetailsService myUserDetailsService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .cors().and()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers( "/").permitAll()
        .antMatchers("/api/auth/storeauthcode").permitAll()
    ;
  }

//  @Override
//  public void configure(AuthenticationManagerBuilder auth) throws Exception {
//    auth
//        .userDetailsService(myUserDetailsService)
//        .passwordEncoder(myUserDetailsService.getEncoder());
//  }
}
