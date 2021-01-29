package com.movieknights.server.services;

import com.movieknights.server.entities.Genre;
import com.movieknights.server.entities.Movie;
import com.movieknights.server.entities.Person;
import com.movieknights.server.relationships.HasActor;
import com.movieknights.server.repos.GenreRepo;
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
    @Autowired
    private GenreRepo genreRepo;

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        for(int i = 1; i < 101; i++) {
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
        Optional<Movie> optional = movieRepo.findById((long) id);
        if(optional.isPresent()) {
            return optional.get();
        }

        Map<String, Object> movieMap = restTemplate.getForObject("https://api.themoviedb.org/3/movie/" + id + "?api_key=7641e2c988f78099d675e3e5a90a9a56&language=sv", Map.class);
        if(movieMap == null) return null;

        Map<String, Object> creditsMap = restTemplate.getForObject("https://api.themoviedb.org/3/movie/" + id + "/credits?api_key=7641e2c988f78099d675e3e5a90a9a56", Map.class);
        if(creditsMap == null) return null;

        HashSet<Genre> genres = new HashSet(getGenresForMovie((List<LinkedHashMap>) movieMap.get("genres"), id));
        HashSet<Person> directors = new HashSet();
        HashSet<HasActor> cast = new HashSet();
        HashSet<Person> composers = new HashSet();

        Date releaseDate = new Date();

        try{
            cast = new HashSet<>(getCastByMovieId(creditsMap));
            directors = new HashSet<>(getCrewByMovieId(creditsMap, "Director"));
            composers = new HashSet<>(getCrewByMovieId(creditsMap, "Original Music Composer"));
        }
        catch (Exception e){
            System.out.println(e);
        }

        try {
            releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) movieMap.get("release_date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String posterPath = "http://filmtropolis.se/noimage.png";
        if(movieMap.get("poster_path") != null) {
            posterPath = "https://image.tmdb.org/t/p/original" + movieMap.get("poster_path");
        }

        String backdropPath = "http://filmtropolis.se/noimagebackdrop.png";
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

        movieRepo.save(movie);

        return movie;
    }

    public List<Genre> getGenresForMovie(List<LinkedHashMap> genresFromMovie, int id) {
        // keep maybe?
    //      Optional<Movie> optional = movieRepo.findById(id);
//          if(optional.isPresent()) {
//            return optional.get().getGenres();
//        }
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
                    if(!p.get("known_for_department").equals("Acting")) {
                        return null;
                    } else {
                        Map<String, Object> personMap = restTemplate.getForObject("https://api.themoviedb.org/3/person/" + p.get("id") + "?api_key=7641e2c988f78099d675e3e5a90a9a56", Map.class);
                        if(personMap == null) return null;

                        Person person = createPerson(p, personMap);

                        cast.add(new HasActor(person, (String) p.get("character"), (int) p.get("order")));

                        return person;
                    }
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
                        Map<String, Object> personMap = restTemplate.getForObject("https://api.themoviedb.org/3/person/" + p.get("id") + "?api_key=7641e2c988f78099d675e3e5a90a9a56", Map.class);
                        if(personMap == null) return null;

                        Person person = createPerson(p, personMap);

                        return person;
                    }
                })
                .filter(p ->  p != null )
                .collect(Collectors.toList());

        return credits;
    }

    private Person createPerson(Map<String, Object> p, Map<String, Object> personMap) {
        Date dob = null;
        Date dod = null;
        try {
            if (personMap.get("birthday") != null) dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) personMap.get("birthday"));
            else if (personMap.get("deathday") != null) dod = new SimpleDateFormat("yyyy-MM-dd").parse((String) personMap.get("deathday"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String profilePath = "http://filmtropolis.se/noimage.png";
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
}
