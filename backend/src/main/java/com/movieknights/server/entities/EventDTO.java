package com.movieknights.server.entities;


import com.google.api.client.util.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private String title;
    private DateTime start;
    private DateTime end;

}
