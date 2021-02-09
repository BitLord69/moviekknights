package com.movieknights.server.controllers;

import com.movieknights.server.entities.Person;
import com.movieknights.server.entities.PersonMovie;
import com.movieknights.server.services.PersonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("rest/person")
public class PersonController {

  @Autowired
  private PersonService personService;

  @GetMapping("/")
  public List<Person> getAll() {
    return personService.getAllPeople();
  }

//  @GetMapping("/{id}")
//  public Optional<PersonMovie> getPersonById(@PathVariable long id) {
//    return personService.getPersonById(id);
//  }

  @GetMapping("/count")
  public long getCountOfPeopleInDb() {
    return personService.getCount();
  }
}
