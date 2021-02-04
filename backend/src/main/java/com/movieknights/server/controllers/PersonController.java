package com.movieknights.server.controllers;

import com.movieknights.server.entities.Movie;
import com.movieknights.server.repos.PersonRepo;
import com.movieknights.server.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("rest/persons")
public class PersonController {

    @Autowired
    private PersonRepo movieService;

    @GetMapping("/{id}/movielist")
    public Object getPersonsListOfMovies(@PathVariable long id) {
        Object actor = movieService.getAllMoviesPersonWorkedWith(id);
        return actor;
    }
}
