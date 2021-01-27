package com.movieknights.server.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Date;

@Data
@Node("mkperson")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {
    @Id
    private long id;

    private Date dob;
    private Date dod;
    private String name;
    private String profileImgPath;
    private String biography;
    private String homepage;
    private String imdbId;
    private int gender;
    private boolean adult;
}
