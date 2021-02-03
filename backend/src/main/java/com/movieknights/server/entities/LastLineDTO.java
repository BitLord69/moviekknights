package com.movieknights.server.entities;

import lombok.Data;

@Data
public class LastLineDTO {

    private boolean adult;
    private boolean video;
    private long id;
    private String original_title;
    private float popularity;
}
