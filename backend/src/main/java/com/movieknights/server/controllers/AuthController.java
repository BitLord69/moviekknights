package com.movieknights.server.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.movieknights.server.entities.User;
import com.movieknights.server.jwt.JwtResponse;
import com.movieknights.server.jwt.JwtUtils;
import com.movieknights.server.repos.UserRepo;
import com.movieknights.server.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
public class AuthController {
  @Value("${api.google.client_id}")
  private String CLIENT_ID;

  @Value("${api.google.client_secret}")
  private String CLIENT_SECRET;

  @Value("${movieknights.app.jwtSecret}")
  private String PASSWORD_SALT;

  @Autowired
  private UserRepo userRepo;

//  @Autowired
//  AuthenticationManager authenticationManager;

  @Autowired
  JwtUtils jwtUtils;


  @Autowired
  UserDetailsServiceImpl userService;

  @RequestMapping(value = "/storeauthcode", method = RequestMethod.POST)
  public ResponseEntity storeauthcode(@RequestBody String code, @RequestHeader("X-Requested-With") String encoding) {
    if (encoding == null || encoding.isEmpty()) {
      // Without the `X-Requested-With` header, this request could be forged. Aborts.
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    GoogleTokenResponse tokenResponse = null;
    try {
      tokenResponse = new GoogleAuthorizationCodeTokenRequest(
          new NetHttpTransport(),
          JacksonFactory.getDefaultInstance(),
          "https://www.googleapis.com/oauth2/v4/token",
          CLIENT_ID,
          CLIENT_SECRET,
          code,
          "http://localhost:8080") // Make sure you set the correct port
          .execute();
    } catch (IOException e) {
      e.printStackTrace();
      return new ResponseEntity("Error, tokenResponse", HttpStatus.BAD_REQUEST);
    }

    // Get user info
    User user = createOrUpdateUser(tokenResponse);
    if (user == null) {
      return new ResponseEntity("Error, no payload from Google", HttpStatus.BAD_REQUEST);
    }

    // Authenticate and return user details
    return authenticateUser(user);
  }

  @GetMapping("/whoami")
  public ResponseEntity whoAmI() {
    Authentication authentication = ReactiveSecurityContextHolder
                              .getContext()
                              .map(context ->
                                      context.getAuthentication())
                              .block();
    if (authentication == null) {
      System.out.println("whoami authentication is null");
      return ResponseEntity.ok(new NotLoggedInError());
    }

    String username = authentication.getName();

    System.out.println("whoami username is: " + username);

    if (username.equals("anonymousUser")) {
      System.out.println("whoami anonymousUser");
      return ResponseEntity.ok(new NotLoggedInError());
    }
    return ResponseEntity.ok(userRepo.findById(username).get());
  }

  private ResponseEntity<JwtResponse> authenticateUser(User user) {
    System.out.println("username: " + user.getUsername() + ", password:" + user.getPassword());
    String jwt = jwtUtils.generateJwtToken(user.getUsername());
    return ResponseEntity.ok(new JwtResponse(jwt, user.getUsername(), user.getGoogleAccessToken()));
  }

  private User createOrUpdateUser(GoogleTokenResponse tokenResponse) {
    String accessToken = tokenResponse.getAccessToken();
    String refreshToken = tokenResponse.getRefreshToken();
    Long expiresAt = System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000);

    GoogleIdToken.Payload payload = null;
    try {
      payload = tokenResponse.parseIdToken().getPayload();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    User user;
    Optional<User> optUser = userRepo.findById(payload.getEmail());
    user = optUser.orElseGet(User::new);

    String password = payload.getEmail() + PASSWORD_SALT;
    user.setPassword(userService.getEncoder().encode(password));

    user.setExpiresAt(expiresAt);
    user.setGoogleAccessToken(accessToken);
    user.setUsername(payload.getEmail());
    user.setRefreshToken(refreshToken);
    user.setName((String)payload.get("name"));
    user.setPictureUrl((String)payload.get("picture"));

    return userRepo.save(user);
  }
}
