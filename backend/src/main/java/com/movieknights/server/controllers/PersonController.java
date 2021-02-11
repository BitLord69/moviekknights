package com.movieknights.server.controllers;

import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.repos.MovieRepo;
import com.movieknights.server.services.PersonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("rest/person")
public class PersonController {

  @Autowired
  private PersonService personService;

  @Autowired
  private MovieRepo movieRepo;

  @GetMapping("/")
  public List<Person> getAll() {
    return personService.getAllPeople();
  }

  @GetMapping("/director/{id}")
  public List<Movie> getAllMoviesFromDirector(@PathVariable long id) {
    return movieRepo.findAllMoviesFromDirector(id);
  }

  @GetMapping("/composer/{id}")
  public List<Movie> getAllMoviesFromComposer(@PathVariable long id) {
    return movieRepo.findAllMoviesFromComposer(id);
  }

  @GetMapping("/actor/{id}")
  public List<Movie> getAllMoviesFromActor(@PathVariable long id) {
    return personService.getAllMoviesFromActor(id);
  }

  @GetMapping("/count")
  public long getCountOfPeopleInDb() {
    return personService.getCount();
  }
}
