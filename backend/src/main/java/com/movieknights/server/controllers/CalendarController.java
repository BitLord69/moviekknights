package com.movieknights.server.controllers;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.movieknights.server.entities.User;
import com.movieknights.server.repos.UserRepo;
import com.movieknights.server.services.UserDetailsImpl;
import com.movieknights.server.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("rest/calendar")
@SuppressWarnings("deprecation")
public class CalendarController {
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${api.google.api_key}")
  private String API_KEY;

  @Value("${api.google.client_secret}")
  private String GOOGLE_SECRET;

  @Value("${api.google.client_id}")
  private String GOOGLE_ID;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  UserDetailsServiceImpl userService;

  @GetMapping("/freebusy")
  public ResponseEntity getFreeBusy() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
    User user = userRepo.findById(userDetails.getUsername()).get();

    List<User> users = userRepo.findAll();
    DateTime dateMin = new DateTime(new Date());
    DateTime dateMax = new DateTime(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
    GoogleCredential credentials = null;
    try {
      credentials = new GoogleCredential.Builder().setTransport(GoogleNetHttpTransport.newTrustedTransport())
        .setJsonFactory(JacksonFactory.getDefaultInstance())
        .setClientSecrets(GOOGLE_ID, GOOGLE_SECRET)

        .build();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //new GoogleCredential().setAccessToken(user.getGoogleAccessToken());

    Calendar calendar = new Calendar.Builder(
            new NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            credentials)
            .setApplicationName("Movie Nights")
            .build();

     Calendar.Freebusy freebusy = calendar.freebusy();
     FreeBusyRequest freeBusyRequest = new FreeBusyRequest();
     List<FreeBusyRequestItem> items = new ArrayList<>();
     users.forEach(u -> {
       FreeBusyRequestItem freeBusyRequestItem = new FreeBusyRequestItem();
       items.add(freeBusyRequestItem.setId(u.getUsername()));
     });

    freeBusyRequest.setTimeMin(dateMin);
    freeBusyRequest.setTimeMax(dateMax);
    freeBusyRequest.setItems(items);

    Calendar.Freebusy.Query freeBusyQuery = null;
    FreeBusyResponse res = null;
    try {
      res = freebusy.query(freeBusyRequest)
          .setKey(API_KEY)
          .execute();
    } catch (IOException e) {
      e.printStackTrace();
      return new ResponseEntity("Error!!!!!!!! " + e, HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok(res);
  }
}
