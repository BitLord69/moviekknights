package com.movieknights.server.controllers;

import com.movieknights.server.services.MovieService;
import com.movieknights.server.entities.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("rest/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/")
    public List<Movie> getAll() {
        List<Movie> allMovies = movieService.getAllMovies();
        return allMovies;
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable int id) {
        Movie movie = movieService.getMovieById(id);
        return movie;
    }

    @GetMapping("/count")
    public int getCountOfMoviesInDb() {
        return movieService.getCount();
    }
}
