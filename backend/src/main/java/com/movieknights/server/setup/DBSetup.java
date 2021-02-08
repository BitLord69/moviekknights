package com.movieknights.server.setup;

import com.movieknights.server.repos.DBSettingRepo;
import com.movieknights.server.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DBSetup {
    @Autowired
    private DBSettingRepo dbRepo;

    @Autowired
    private MovieService ms;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new DBUpdate(dbRepo, ms), 0, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }
}
