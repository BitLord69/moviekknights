package com.movieknights.server.controllers;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.movieknights.server.entities.User;
import com.movieknights.server.repos.UserRepo;
import com.movieknights.server.services.UserDetailsImpl;
import com.movieknights.server.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("rest/calendar")
@SuppressWarnings("deprecation")
public class CalendarController {

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

        GoogleCredential credentials = new GoogleCredential().setAccessToken(user.getGoogleAccessToken());

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
        try {
          freeBusyQuery = freebusy.query(freeBusyRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return (ResponseEntity) ResponseEntity.noContent();
        }
        System.out.println("freeBusyQ: " + freeBusyQuery);
        return ResponseEntity.ok(freeBusyQuery);
    }
}
