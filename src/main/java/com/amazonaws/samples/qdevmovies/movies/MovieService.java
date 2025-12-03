package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! This here method searches through our treasure chest of movies
     * using various criteria like name, id, and genre. Perfect for finding that
     * specific cinematic treasure ye be seekin'!
     * 
     * @param name Movie name to search for (partial matches allowed, case-insensitive)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by (case-insensitive)
     * @return List of movies matching the search criteria, or empty list if no matches found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Searching for movies with name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = new ArrayList<>(movies);
        
        // Filter by ID first (most specific search)
        if (id != null && id > 0) {
            Optional<Movie> movieById = getMovieById(id);
            return movieById.map(List::of).orElse(new ArrayList<>());
        }
        
        // Filter by movie name (partial match, case-insensitive)
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            results = results.stream()
                    .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                    .collect(Collectors.toList());
        }
        
        // Filter by genre (case-insensitive)
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            results = results.stream()
                    .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                    .collect(Collectors.toList());
        }
        
        logger.info("Search completed! Found {} movies matching the criteria", results.size());
        return results;
    }

    /**
     * Get all unique genres from our movie collection
     * Useful for populating search dropdowns, savvy?
     * 
     * @return List of unique genres
     */
    public List<String> getAllGenres() {
        return movies.stream()
                .map(Movie::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
