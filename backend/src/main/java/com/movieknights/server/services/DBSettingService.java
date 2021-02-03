package com.movieknights.server.services;


import com.movieknights.server.entities.DBSetting;
import com.movieknights.server.repos.DBSettingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DBSettingService {


    @Autowired
    private DBSettingRepo dbSettingRepo;

    public DBSetting checkFileInDB() {
        LocalDate localDate = LocalDate.now().minusDays(1);
        String text = localDate.toString();
        System.out.println("text: " + text);
        System.out.println("localdate: " + localDate);
        Optional<DBSetting> dbSetting = dbSettingRepo.findById(localDate);
        return dbSetting.orElse(null);
    }

    public DBSetting save(DBSetting dbSetting) {
        return dbSettingRepo.save(dbSetting);
    }
}
