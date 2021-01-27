package com.movieknights.server.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/auth")
public class AuthController {
  @Value("${api.google.client_id}")
  private String CLIENT_ID;

  @Value("${server.port}")
  private String SERVER_PORT;

  @Value("${api.google.client_secret}")
  private String CLIENT_SECRET;

  @RequestMapping(value = "/storeauthcode", method = RequestMethod.POST)
  public ResponseEntity storeauthcode(@RequestBody String code, @RequestHeader("X-Requested-With") String encoding) {
    if (encoding == null || encoding.isEmpty()) {
      // Without the `X-Requested-With` header, this request could be forged. Aborts.
      return new ResponseEntity("Error, wrong headers", HttpStatus.BAD_REQUEST);
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
      return null;
    }

    // Store these 3 in your DB
    String accessToken = tokenResponse.getAccessToken();
    String refreshToken = tokenResponse.getRefreshToken();
    Long expiresAt = System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000);

    // Debug purpose only
    System.out.println("accessToken: " + accessToken);
    System.out.println("refreshToken: " + refreshToken);
    System.out.println("expiresAt: " + expiresAt);

    // TODO: 2021-01-27 Return the creates user or JWT token 
    return ResponseEntity.ok("OK");
  }
}
