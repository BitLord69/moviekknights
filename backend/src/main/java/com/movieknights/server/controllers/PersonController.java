package com.movieknights.server.controllers;

import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.relationships.HasActor;
import com.movieknights.server.repos.MovieRepo;
import com.movieknights.server.repos.PersonRepo;
import com.movieknights.server.services.MovieService;
import com.movieknights.server.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("rest/persons")
public class PersonController {

    @Autowired
    private MovieRepo personService;

    @GetMapping("/{id}/movielist")
    public List<Movie> getListOfMoviesByPerson(@PathVariable long id) {
        return personService.getAllMoviesPersonWorkedWith(id);
    }
}
