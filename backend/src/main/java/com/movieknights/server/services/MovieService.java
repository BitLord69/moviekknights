package com.movieknights.server.services;

import com.movieknights.server.entities.Genre;
import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.repos.MovieRepo;
import com.movieknights.server.repos.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private MovieRepo movieRepo;
    @Autowired
    private PersonRepo personRepo;

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();

        for(int i = 11; i < 12; i++) {
            try {
                movies.add(getMovieById(i));
            }
            catch (Exception e) {
                System.out.println(e);
            }

        }

        return movies;
    }

    public Movie getMovieById(int id) {
//        Optional<Movie> optional = movieRepo.findById(id);
//        if(optional.isPresent()) {
//            return optional.get();
//        }

        Map<String, Object> movieMap = restTemplate.getForObject("https://api.themoviedb.org/3/movie/" + id + "?api_key=7641e2c988f78099d675e3e5a90a9a56&language=sv", Map.class);
        if(movieMap == null) return null;

        HashSet<Genre> genres = new HashSet(getGenresForMovie((List<LinkedHashMap>) movieMap.get("genres")));
        HashSet<Person> directors = new HashSet();
        HashSet<Person> cast = new HashSet();
        HashSet<Person> composers = new HashSet();

        Date releaseDate = new Date();

        try{
            cast = new HashSet<>(getCastOrCrewByMovieId(id,"cast", "known_for_department", "Acting"));
            directors = new HashSet<>(getCastOrCrewByMovieId(id,"crew", "job", "Director"));
            composers = new HashSet<>(getCastOrCrewByMovieId(id,"crew", "job", "Original Music Composer"));
        }
        catch (Exception e){
            System.out.println(e);
        }

        try {
            releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) movieMap.get("release_date"));
        } catch (ParseException e) {
            e.printStackTrace();
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
                "https://image.tmdb.org/t/p/original" + movieMap.get("poster_path"),
                "https://image.tmdb.org/t/p/original" + movieMap.get("backdrop_path"),
                releaseDate,
                (int) movieMap.get("runtime"),
                (double) movieMap.get("popularity"),
                genres,
                directors,
                cast,
                composers,
                (boolean) movieMap.get("adult")
        );

        //movieRepo.save(movie);

        return movie;
    }

    public List<Genre> getGenresForMovie(List<LinkedHashMap> genresFromMovie) {
//        Optional<Movie> optional = movieRepo.findById(id);
//        if(optional.isPresent()) {
//            return optional.get().getCredits();
//        }

        List<Genre> genres = new ArrayList<>();

        genresFromMovie.forEach(o ->
        {
            genres.add(new Genre((int) o.get("id"), (String) o.get("name")));
        });


        return genres;
    }

    public List<Person> getCastOrCrewByMovieId(int id, String listToGetFrom, String department, String typeOfWork) {
//        Optional<Movie> optional = movieRepo.findById(id);
//        if(optional.isPresent()) {
//            return optional.get().getCredits();
//        }

        Map<String, Object> creditsMap = restTemplate.getForObject("https://api.themoviedb.org/3/movie/" + id + "/credits?api_key=7641e2c988f78099d675e3e5a90a9a56", Map.class);
        if(creditsMap == null) return null;

        List<Person> credits = ((List<Map<String, Object>>) creditsMap.get(listToGetFrom))
                .stream()
                .map(p -> {
//                    Optional<Cast> castOptional = castRepo.findById((int) c.get("id"));
//                    if(castOptional.isPresent()) {
//                        return castOptional.get();
//                    }
                    Map<String, Object> personMap = restTemplate.getForObject("https://api.themoviedb.org/3/person/" + p.get("id") + "?api_key=7641e2c988f78099d675e3e5a90a9a56", Map.class);
                    if(personMap == null) return null;
                    // TODO: 2021-01-28 LOOK IF PROFILE_PATH IS NULL AND DO SOMETHING ABOUT IT
                    Date dob = null;
                    Date dod = null;
                    try {
                        if (personMap.get("birthday") != null) dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) personMap.get("birthday"));
                        else if (personMap.get("deathday") != null) dod = new SimpleDateFormat("yyyy-MM-dd").parse((String) personMap.get("deathday"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Person person = new Person(
                            (int) p.get("id"),
                            dob,
                            dod,
                            (String) p.get("name"),
                            "https://image.tmdb.org/t/p/original" + (String) p.get("profile_path"),
                            (String) personMap.get("biography"),
                            (String) personMap.get("homepage"),
                            (String) personMap.get("imdb_id"),
                            (int) p.get("gender"),
                            (boolean) p.get("adult")
                    );
                    if(p.get(department).equals(typeOfWork)) {
                        //personRepo.save(person);
                        return person;
                    } else {
                        return null;
                    }
                })
                .filter(p ->  p != null )
                .collect(Collectors.toList());


        return credits;
    }
}
