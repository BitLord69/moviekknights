package com.movieknights.server.services;

import com.movieknights.server.entities.User;
import com.movieknights.server.repos.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepo userRepo;

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
  public BCryptPasswordEncoder getEncoder() { return encoder; }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepo.findById(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    return UserDetailsImpl.build(user);
  }

//  @PostConstruct
//  private void createStartupEntities(){
//  }

//  public User addUser(String email, String name, String pictureUrl, String password, String accessToken, String refreshToken, Long expiresAt){
//    User user = new User(email, name, pictureUrl, encoder.encode(password), accessToken, refreshToken, expiresAt);
//    try {
//      return userRepo.save(user);
//    } catch (Exception ex) {
//      ex.printStackTrace();
//    }
//    return null;
//  }
}