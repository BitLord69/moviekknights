package com.movieknights.server.services;

import com.movieknights.server.entities.Genre;
import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.relationships.HasActor;
import com.movieknights.server.repos.MovieRepo;
import com.movieknights.server.repos.PersonRepo;
import com.movieknights.server.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Math.random;

@Service
public class MovieService {
  public static final int MAX_RETRIES = 10;
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${tmdb_key}")
  private String TMDB_KEY;

  @Autowired
  private MovieRepo movieRepo;

  @Autowired
  private PersonRepo personRepo;


  public List<Movie> getAllMovies() {
    Instant start = Instant.now();
    int countFor404 = 0;
    List<Movie> movies = new ArrayList<>();
    for(int i = 23301; i <= 24000; i++) {
      try {
        movies.add(getMovieById(i));
      }
      catch (Exception e) {
        countFor404++;
        System.out.println("ID: " + i);
        System.out.println(e);
        System.out.println("Antal 404: " + countFor404);
      }
    }
    Instant end = Instant.now();
    Duration between = Duration.between(start, end);
    System.out.println(between.getSeconds());

    return movies;
  }

  public Movie getMovieById(long id) {
    Optional<Movie> optional = movieRepo.findById(id);
    if(optional.isPresent()) {
      System.out.println("ID " + TextUtil.pimpString(id, TextUtil.LEVEL_INFO) + " is already in the database!");
      return optional.get();
    }

    Map<String, Object> movieMap = null;

    try {
      movieMap = restTemplate.getForObject("https://api.themoviedb.org/3/movie/"
          + id + "?api_key=" + TMDB_KEY + "&language=sv&append_to_response=credits", Map.class);
    } catch (HttpClientErrorException e) {
      System.out.printf("Error getting movie with id: %s!\nError: %s\n",
          TextUtil.pimpString(id, TextUtil.LEVEL_BOLD),
          TextUtil.pimpString(e.toString(), TextUtil.LEVEL_BOLD));
      return null;
    }

    if (movieMap == null) {
      return null;
    }

    LinkedHashMap<String, Object> creditsMap = (LinkedHashMap<String, Object>) movieMap.get("credits");
    if(creditsMap == null) return null;

    Movie movie = createMovie(movieMap, creditsMap, id);

    AtomicBoolean isOK = new AtomicBoolean(false);
    AtomicInteger failCounter = new AtomicInteger(0);

    while (!isOK.get() && failCounter.get() < MAX_RETRIES) {
      try {
        movieRepo.save(movie);
        isOK.set(true);
      } catch (Exception e) {
        failCounter.getAndIncrement();
        System.out.printf("Error in %s when trying to save movie: %s, %s, number of retries: %s\nError: %s\n",
            TextUtil.pimpString("getMovieById", TextUtil.LEVEL_INFO),
            TextUtil.pimpString(movie.getTitle(), TextUtil.LEVEL_WARNING),
            TextUtil.pimpString(movie.getMovieId(), TextUtil.LEVEL_WARNING),
            TextUtil.pimpString(failCounter.get(), TextUtil.LEVEL_WARNING),
            TextUtil.pimpString(e.toString(), TextUtil.LEVEL_WARNING));

        // Let the thread sleep so the other calls can be resolved
        try {
          Thread.sleep((long)(random() * 200) + 200);
        } catch (InterruptedException interruptedException) {
          interruptedException.printStackTrace();
        }
      }
    }

    if (isOK.get()) {
      System.out.printf("Movie '%s' with id %s saved in the database!\n",
          TextUtil.pimpString(movie.getTitle(), TextUtil.LEVEL_INFO),
          TextUtil.pimpString(id, TextUtil.LEVEL_INFO));
    } else {
      System.out.printf("Max retries encountered without managing to save movie '%s' (%s)\n",
          TextUtil.pimpString(movie.getTitle(), TextUtil.LEVEL_INFO),
          TextUtil.pimpString(id, TextUtil.LEVEL_INFO));
    }
    return movie;
  }

  public List<Genre> getGenresForMovie(List<LinkedHashMap> genresFromMovie, long id) {
    List<Genre> genres = new ArrayList<>();

    genresFromMovie.forEach(o ->
    {
      genres.add(new Genre((int) o.get("id"), (String) o.get("name")));
    });

    return genres;
  }

  public List<HasActor> getCastByMovieId(Map<String, Object> castMap) {
    List<HasActor> cast = new ArrayList<>();
    List<Person> credits = ((List<Map<String, Object>>) castMap.get("cast"))
        .stream()
        .map(p -> {
          long id = (int) p.get("id");
          try {
            Optional<Person> optional = personRepo.findById(id);
            if (optional.isPresent()) {
              cast.add(new HasActor(optional.get(), (String) p.get("character"), (int) p.get("order")));
              return optional.get();
            }
          } catch (Exception e) {
            System.out.printf("getCastByMovieId - Error doing a %s on person with the id %s\nError: %s\n",
                TextUtil.pimpString("personRepo.findById", TextUtil.LEVEL_INFO),
                TextUtil.pimpString(id, TextUtil.LEVEL_INFO),
                TextUtil.pimpString(e.toString(), TextUtil.LEVEL_WARNING));
          }

          Person person = createPerson(p);
          cast.add(new HasActor(person, (String) p.get("character"), (int) p.get("order")));
          return person;
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    return cast;
  }

  public List<Person> getCrewByMovieId(Map<String, Object> castMap, String typeOfJob) {
    List<Person> credits = ((List<Map<String, Object>>) castMap.get("crew"))
        .stream()
        .map(p -> {
          if(!p.get("job").equals(typeOfJob)) {
            return null;
          } else {
            long id = (int) p.get("id");
            try {
              Optional<Person> optional = personRepo.findById(id);
              if(optional.isPresent()) {
                return optional.get();
              }
            } catch (Exception e) {
              System.out.printf("getCrewByMovieId - Error doing a %s on person with the id %s\nError: %s\n",
                  TextUtil.pimpString("personRepo.findById", TextUtil.LEVEL_INFO),
                  TextUtil.pimpString(id, TextUtil.LEVEL_INFO),
                  TextUtil.pimpString(e.toString(), TextUtil.LEVEL_WARNING));
            }

            return createPerson(p);
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    return credits;
  }

  private Movie createMovie(Map<String, Object> movieMap, Map<String, Object> creditsMap, long id) {
    HashSet<Genre> genres = new HashSet(getGenresForMovie((List<LinkedHashMap>) movieMap.get("genres"), id));
    HashSet<Person> directors = new HashSet<>(getCrewByMovieId(creditsMap, "Director"));
    HashSet<HasActor> cast = new HashSet<>(getCastByMovieId(creditsMap));
    HashSet<Person> composers = new HashSet<>(getCrewByMovieId(creditsMap, "Original Music Composer"));
    Date releaseDate = null;
    String posterPath = null;
    String backdropPath = null;

    try{
      SimpleDateFormat simpleReleaseDate = new SimpleDateFormat("yyyy-MM-dd");
      simpleReleaseDate.setTimeZone(TimeZone.getTimeZone("UTC"));
      releaseDate = simpleReleaseDate.parse((String) movieMap.get("release_date"));
    } catch (ParseException e) {
      System.out.printf("Release date not defined in TMDB, defaulting to null.\n");
    }

    if(movieMap.get("poster_path") != null) {
      posterPath = "https://image.tmdb.org/t/p/original" + movieMap.get("poster_path");
    }

    if(movieMap.get("backdrop_path") != null) {
      backdropPath = "https://image.tmdb.org/t/p/original" + movieMap.get("backdrop_path");
    }

    int runtime;
    try {
      runtime = (int)movieMap.get("runtime");
    } catch (NullPointerException e) {
      runtime = 0;
      System.out.printf("Run time is null, defaulting to zero!\n");
    }

    Movie movie = new Movie(
        (int) movieMap.get("id"),
        (String) movieMap.get("title"),
        (String) movieMap.get("original_title"),
        (String) movieMap.get("original_language"),
        (String) movieMap.get("tagline"),
        (String) movieMap.get("overview"),
        (String) movieMap.get("imdb_id"),
        (String) movieMap.get("status"),
        posterPath,
        backdropPath,
        releaseDate,
        runtime,
        (double) movieMap.get("popularity"),
        genres,
        directors,
        cast,
        composers,
        (boolean) movieMap.get("adult")
    );
    System.out.printf("Movie: %s (%d) is created....\n", TextUtil.pimpString(movie.getTitle(), TextUtil.LEVEL_INFO), movie.getMovieId());
    return movie;
  }

  private Person createPerson(Map<String, Object> p) {
    Map<String, Object> personMap = restTemplate.getForObject("https://api.themoviedb.org/3/person/"
        + p.get("id") + "?api_key=" + TMDB_KEY + "&language=sv", Map.class);
    if(personMap == null) return null;

    Date dob = null;
    Date dod = null;
    String profilePath = null;
    try {
      if (personMap.get("birthday") != null) {
        SimpleDateFormat simpleDob = new SimpleDateFormat("yyyy-MM-dd");
        simpleDob.setTimeZone(TimeZone.getTimeZone("UTC"));
        dob = simpleDob.parse((String) personMap.get("birthday"));
      }
      if (personMap.get("deathday") != null) {
        SimpleDateFormat simpleDod = new SimpleDateFormat("yyyy-MM-dd");
        simpleDod.setTimeZone(TimeZone.getTimeZone("UTC"));
        dod = simpleDod.parse((String) personMap.get("deathday"));
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }

    if(p.get("profile_path") != null) {
      profilePath = "https://image.tmdb.org/t/p/original" + p.get("profile_path");
    }

    Person person = new Person(
        (int) p.get("id"),
        dob,
        dod,
        (String) p.get("name"),
        profilePath,
        (String) personMap.get("biography"),
        (String) personMap.get("homepage"),
        (String) personMap.get("imdb_id"),
        (int) p.get("gender"),
        (boolean) p.get("adult")
    );

    return person;
  }

  public long getCount() {
    return movieRepo.count();
  }

  public List<Movie> getMoviesFromDb() {
    return movieRepo.findAll(Sort.by(Sort.Direction.DESC, "movieId"));
  }

  public List<Movie> getMoviesFromPagination(int page) {
    Page<Movie> pages = movieRepo.findAll(PageRequest.of(page, 18));
    List<Movie> content = pages.getContent();
    return content;
  }

  public List<Movie> getMoviesBySearch(String searchTerm) {
    return movieRepo.findTop50ByTitleIsStartingWithIgnoreCase(searchTerm);
  }
}
