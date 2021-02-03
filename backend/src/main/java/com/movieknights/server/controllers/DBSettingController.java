package com.movieknights.server.controllers;

import com.movieknights.server.entities.DBSetting;
import com.movieknights.server.services.DBSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/date")
public class DBSettingController {


    @Autowired
    private DBSettingService dbSettingService;

    @GetMapping
    public DBSetting getDate() {
        return dbSettingService.checkFileInDB();
    }

    @PostMapping
    public DBSetting postDate(@RequestBody DBSetting dbSetting) {
        return dbSettingService.save(dbSetting);
    }

}
