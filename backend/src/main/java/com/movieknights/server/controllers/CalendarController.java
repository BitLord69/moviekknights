package com.movieknights.server.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.movieknights.server.entities.EventDTO;
import com.movieknights.server.entities.User;
import com.movieknights.server.repos.UserRepo;
import com.movieknights.server.services.UserDetailsImpl;
import com.movieknights.server.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

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

  @GetMapping("/freebusy/{start}/{end}")
  public ResponseEntity<?> getFreeBusy(@PathVariable DateTime start, @PathVariable DateTime end) {
    List<User> users = userRepo.findAll();
    System.out.printf("\nStart: %s\nEnd: %s\n", start, end);
    Calendar calendar = getGoogleCalendar();

    Calendar.Freebusy freebusy = calendar.freebusy();
    FreeBusyRequest freeBusyRequest = new FreeBusyRequest();
    List<FreeBusyRequestItem> items = new ArrayList<>();
    users.forEach(u -> {
      FreeBusyRequestItem freeBusyRequestItem = new FreeBusyRequestItem();
      items.add(freeBusyRequestItem.setId(u.getUsername()));
    });

    freeBusyRequest.setTimeMin(start);
    freeBusyRequest.setTimeMax(end);
    freeBusyRequest.setItems(items);

    Calendar.Freebusy.Query freeBusyQuery = null;
    FreeBusyResponse res = null;
    try {
      res = freebusy.query(freeBusyRequest)
              .setKey(API_KEY)
              .execute();
    } catch (IOException e) {
      e.printStackTrace();
      return new ResponseEntity<>("Error!!!!!!!! " + e, HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok(res);
  }

  @GetMapping("/personal")
  public ResponseEntity<?> getPersonal() {
    DateTime dateMin = new DateTime(String.valueOf(LocalDateTime.now().atOffset(ZoneOffset.ofHours(1)).withDayOfMonth(1).withSecond(0).withMinute(0).withHour(0)));
    DateTime dateMax = new DateTime(String.valueOf(LocalDateTime.now().atOffset(ZoneOffset.ofHours(1)).with(TemporalAdjusters.lastDayOfMonth()).withSecond(59).withMinute(59).withHour(23)));
    User user = getUser();

    Events events = null;

    Calendar calendar = getGoogleCalendar();
    try {
      events = calendar.events()
          .list(user.getUsername())
          .setTimeMin(dateMin)
          .setTimeMax(dateMax)
          .setKey(API_KEY)
          .execute();
    } catch (IOException e) {
      e.printStackTrace();
      return new ResponseEntity<>("Error!!!!!!!! " , HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok(events.getItems());
  }

  private Calendar getGoogleCalendar() {
    GoogleCredential credentials = null;
    try {
      credentials = new GoogleCredential.Builder().setTransport(GoogleNetHttpTransport.newTrustedTransport())
              .setJsonFactory(JacksonFactory.getDefaultInstance())
              .setClientSecrets(GOOGLE_ID, GOOGLE_SECRET)
              .build();
    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }

    return new Calendar.Builder(
            new NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            credentials)
            .setApplicationName("Movie Nights")
            .build();
  }

  @PostMapping("/add")
  public ResponseEntity<?> addEvent(@RequestBody EventDTO eventDTO) {
    List<User> users = userRepo.findAll();
    User user = getUser();
    Calendar calendar = getGoogleCalendar();

    EventDateTime start = new EventDateTime();
    EventDateTime end = new EventDateTime();
    start.setDateTime(eventDTO.getStart());
    end.setDateTime(eventDTO.getEnd());
    Event event = new Event().setSummary(eventDTO.getTitle()).setStart(start).setEnd(end);
    List<EventAttendee> attendees = new ArrayList<>();

    for (User u : users) {
      if (!u.getUsername().equals(user.getUsername())) {
        attendees.add(new EventAttendee().setEmail(u.getUsername()));
      }
    }
    event.setAttendees(attendees);
    try {
      event = calendar.events().insert(user.getUsername(), event).setOauthToken(user.getGoogleAccessToken()).setKey(API_KEY).execute();
    } catch (IOException e) {
      return new ResponseEntity<>("Error!!!!!!!! " + e, HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok("SUCCESS!!!" + event);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteEvent(@PathVariable String id){
    System.out.println("In DELETE_EVENT: " + id);
    User user = getUser();
    Calendar calendar = getGoogleCalendar();

    try {
      calendar.events().delete(user.getUsername(), id).setOauthToken(user.getGoogleAccessToken()).setKey(API_KEY).execute();
    } catch (IOException e) {
      return new ResponseEntity<>("Unable to remove event!" + e, HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok("Event successfully removed!");
  }

  private User getUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    return userRepo.findById(userDetails.getUsername()).get();
  }
}

