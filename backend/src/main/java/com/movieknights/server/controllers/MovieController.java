package com.movieknights.server.controllers;

import com.movieknights.server.services.MovieService;
import com.movieknights.server.entities.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @GetMapping(value="/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Movie>> getAll() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/count")
    public Mono<Long> getCountOfMoviesInDb() {
        return movieService.getCount();
    }

    @GetMapping(value="/db", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public
    Flux<Movie> getTheMoviesFromDb() {
        return movieService.getMoviesFromDb();
    }
}
