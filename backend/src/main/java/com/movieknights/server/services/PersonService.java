package com.movieknights.server.services;

import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.relationships.HasActor;
import com.movieknights.server.repos.MovieRepo;
import com.movieknights.server.repos.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
  @Autowired
  private PersonRepo personRepo;

  @Autowired
  private MovieRepo movieRepo;

//  @Autowired
//  private PersonMovieRepo personMovieRepo;

//  public Optional<PersonMovie> getPersonById(long id) {
//    return personMovieRepo.findById(id);
//  }

  public List<Person> getAllPeople() {
    return null;
  }

  public long getCount() {
    return personRepo.count();
  }

  public List<Movie> getAllMoviesFromActor(long id) {
    List<Movie> moviesToReturn = new ArrayList<>();
    List<Movie> moviesFromDb = movieRepo.findAllMoviesFromActor(id);
    moviesFromDb.forEach(movie -> {
      Optional<Movie> optional = movieRepo.findById(movie.getMovieId());
      moviesToReturn.add(optional.get());
    });
    return moviesToReturn;
  }
}
