package com.movieknights.server.services;

import com.movieknights.server.entities.Person;
import com.movieknights.server.repos.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
  @Autowired
  private PersonRepo personRepo;

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
}
