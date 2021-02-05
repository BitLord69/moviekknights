package com.movieknights.server.services;

import com.movieknights.server.entities.Genre;
import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.relationships.HasActor;
import com.movieknights.server.repos.MovieRepo;
import com.movieknights.server.repos.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        int countFor404 = 0;
        List<Movie> movies = new ArrayList<>();
        for(int i = 22001; i <= 23000; i++) {
            try {
                movies.add(getMovieById(i));
                System.out.println("ID " + i + " skapad!");
            }
            catch (Exception e) {
                countFor404++;
                System.out.println("ID: " + i);
                System.out.println(e);
                System.out.println("Antal 404: " + countFor404);
            }
        }
        return movies;
    }

    public Movie getMovieById(long id) {
        //Optional<Movie> optional = movieRepo.findMovieByMovieId((long) id);
        Optional<Movie> optional = movieRepo.findById(id);
        if(optional.isPresent()) {
            return optional.get();
        }

        Map<String, Object> movieMap = restTemplate.getForObject("https://api.themoviedb.org/3/movie/"
                + id + "?api_key=" + TMDB_KEY + "&language=sv&append_to_response=credits", Map.class);
        if(movieMap == null) {
            return null;
        }

        LinkedHashMap<String, Object> creditsMap = (LinkedHashMap<String, Object>) movieMap.get("credits");
        if(creditsMap == null) return null;

        Movie movie = createMovie(movieMap, creditsMap, id);

        movieRepo.save(movie);

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
                    Optional<Person> optional = personRepo.findById(id);
                    if(optional.isPresent()) {
                        cast.add(new HasActor(optional.get(), (String) p.get("character"), (int) p.get("order")));
                        return optional.get();
                    }

                    Person person = createPerson(p);

                    cast.add(new HasActor(person, (String) p.get("character"), (int) p.get("order")));

                    return person;
                })
                .filter(p ->  p != null )
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
                        Optional<Person> optional = personRepo.findById(id);
                        if(optional.isPresent()) {
                            return optional.get();
                        }

                        Person person = createPerson(p);

                        return person;
                    }
                })
                .filter(p ->  p != null )
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
            releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) movieMap.get("release_date"));
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
                + p.get("id") + "?api_key=" + TMDB_KEY, Map.class);
        if(personMap == null) return null;

        Date dob = null;
        Date dod = null;
        String profilePath = null;
        try {
            if (personMap.get("birthday") != null) dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) personMap.get("birthday"));
            else if (personMap.get("deathday") != null) dod = new SimpleDateFormat("yyyy-MM-dd").parse((String) personMap.get("deathday"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(p.get("profile_path") != null) {
            profilePath = "https://image.tmdb.org/t/p/original" + (String) p.get("profile_path");
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
        return movieRepo.findAll(Sort.by(Sort.Direction.DESC, "popularity"));
    }
}
