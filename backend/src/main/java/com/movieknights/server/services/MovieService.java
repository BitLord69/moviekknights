package com.movieknights.server.services;

import com.movieknights.server.entities.Genre;
import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.relationships.HasActor;
import com.movieknights.server.repos.MovieRepo;
import com.movieknights.server.repos.PersonRepo;
import com.movieknights.server.utils.TextUtil;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {
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


        System.out.println();
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
                                TextUtil.pimpString(id, TextUtil.LEVEL_WARNING),
                                TextUtil.pimpString(e.toString(), TextUtil.LEVEL_WARNING));
            return null;
        }

        if (movieMap == null) {
            return null;
        }

        LinkedHashMap<String, Object> creditsMap = (LinkedHashMap<String, Object>) movieMap.get("credits");
        if(creditsMap == null) return null;

        Movie movie = createMovie(movieMap, creditsMap, id);

        try {
            movieRepo.save(movie);
        } catch(Exception e) {
            System.out.printf("Error in %s when trying to save movie: %s, %s\nError: %s\n",
                    TextUtil.pimpString("getMovieById", TextUtil.LEVEL_INFO),
                    TextUtil.pimpString(movie.getTitle(), TextUtil.LEVEL_WARNING),
                    TextUtil.pimpString(movie.getMovieId(), TextUtil.LEVEL_WARNING),
                    TextUtil.pimpString(e.toString(), TextUtil.LEVEL_WARNING));
        }

        System.out.printf("Movie '%s' with id %s  saved in the database!\n",
                            TextUtil.pimpString(movie.getTitle(), TextUtil.LEVEL_INFO),
                            TextUtil.pimpString(id, TextUtil.LEVEL_INFO));
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
            e.printStackTrace();
        }

        if(movieMap.get("poster_path") != null) {
            posterPath = "https://image.tmdb.org/t/p/original" + movieMap.get("poster_path");
        }

        if(movieMap.get("backdrop_path") != null) {
            backdropPath = "https://image.tmdb.org/t/p/original" + movieMap.get("backdrop_path");
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
                (int) movieMap.get("runtime"),
                (double) movieMap.get("popularity"),
                genres,
                directors,
                cast,
                composers,
                (boolean) movieMap.get("adult")
        );
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
//        // TODO: 2021-02-06 Ask Alex about this!
//        // Save the person here so it exists for other threads... good or not?
//        try {
//            personRepo.save(person);
//        } catch (Exception e) {
//            System.out.printf("Error doing trying to save in %s: person with the id %s\nError: %s\n",
//                TextUtil.pimpString("createPerson", TextUtil.LEVEL_INFO),
//                TextUtil.pimpString(person.getId(), TextUtil.LEVEL_INFO),
//                TextUtil.pimpString(e.toString(), TextUtil.LEVEL_WARNING));
//        }

        return person;
    }

    public long getCount() {
        return movieRepo.count();
    }

    public List<Movie> getMoviesFromDb() {
        return movieRepo.findAll(Sort.by(Sort.Direction.DESC, "movieId"));
    }
}
