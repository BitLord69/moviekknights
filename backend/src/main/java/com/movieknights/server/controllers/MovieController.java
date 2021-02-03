package com.movieknights.server.controllers;

import com.movieknights.server.services.MovieService;
import com.movieknights.server.entities.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("rest/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/")
    public Flux<List<Movie>> getAll() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/count")
    public long getCountOfMoviesInDb() {
        return movieService.getCount();
    }

    @GetMapping("/db")
    public List<Movie> getTheMoviesFromDb() {
        return movieService.getMoviesFromDb();
    }
}
