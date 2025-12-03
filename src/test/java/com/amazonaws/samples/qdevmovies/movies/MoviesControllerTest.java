package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy! Unit tests for the MoviesController
 * Testing our ship's navigation through the movie waters, savvy?
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with pirate-worthy test data
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if (id == 2L) {
                    return Optional.of(new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                // Filter by ID first (most specific)
                if (id != null && id > 0) {
                    Optional<Movie> movieById = getMovieById(id);
                    return movieById.map(List::of).orElse(new ArrayList<>());
                }
                
                // Filter by name
                if (name != null && !name.trim().isEmpty()) {
                    String searchName = name.trim().toLowerCase();
                    for (Movie movie : allMovies) {
                        if (movie.getMovieName().toLowerCase().contains(searchName)) {
                            results.add(movie);
                        }
                    }
                } else {
                    results.addAll(allMovies);
                }
                
                // Filter by genre
                if (genre != null && !genre.trim().isEmpty()) {
                    String searchGenre = genre.trim().toLowerCase();
                    results.removeIf(movie -> !movie.getGenre().toLowerCase().contains(searchGenre));
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies template with all movies and genres")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        // Verify model attributes
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("genres"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size());
        
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) model.getAttribute("genres");
        assertEquals(2, genres.size());
        assertTrue(genres.contains("Action"));
        assertTrue(genres.contains("Drama"));
    }

    @Test
    @DisplayName("Should return movie details for valid ID")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        assertTrue(model.containsAttribute("movie"));
        assertTrue(model.containsAttribute("movieIcon"));
        assertTrue(model.containsAttribute("allReviews"));
    }

    @Test
    @DisplayName("Should return error template for invalid movie ID")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        
        assertNotNull(result);
        assertEquals("error", result);
        
        assertTrue(model.containsAttribute("title"));
        assertTrue(model.containsAttribute("message"));
    }

    @Test
    @DisplayName("Should search movies and return HTML template")
    public void testSearchMovies_HtmlFormat() {
        String result = (String) moviesController.searchMovies("test", null, null, "html", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        // Verify search results in model
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("genres"));
        assertTrue(model.containsAttribute("searchName"));
        assertTrue(model.containsAttribute("message"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should search movies and return JSON response")
    public void testSearchMovies_JsonFormat() {
        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> result = (ResponseEntity<Map<String, Object>>) 
            moviesController.searchMovies("test", null, null, "json", model);
        
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should search by movie ID and return single result")
    public void testSearchMovies_ById() {
        String result = (String) moviesController.searchMovies(null, 1L, null, "html", model);
        
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals(1L, movies.get(0).getId());
    }

    @Test
    @DisplayName("Should search by genre and return filtered results")
    public void testSearchMovies_ByGenre() {
        String result = (String) moviesController.searchMovies(null, null, "drama", "html", model);
        
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Drama", movies.get(0).getGenre());
    }

    @Test
    @DisplayName("Should return error for invalid movie ID in JSON format")
    public void testSearchMovies_InvalidId_JsonFormat() {
        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> result = (ResponseEntity<Map<String, Object>>) 
            moviesController.searchMovies(null, -1L, null, "json", model);
        
        assertNotNull(result);
        assertEquals(400, result.getStatusCode().value());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.containsKey("error"));
    }

    @Test
    @DisplayName("Should return error for invalid movie ID in HTML format")
    public void testSearchMovies_InvalidId_HtmlFormat() {
        String result = (String) moviesController.searchMovies(null, -1L, null, "html", model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("error"));
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("genres"));
    }

    @Test
    @DisplayName("Should return empty results for non-existent movie name")
    public void testSearchMovies_NoResults() {
        String result = (String) moviesController.searchMovies("NonExistentMovie", null, null, "html", model);
        
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
        
        String message = (String) model.getAttribute("message");
        assertNotNull(message);
        assertTrue(message.contains("No movies found"));
    }

    @Test
    @DisplayName("Should handle API search endpoint")
    public void testSearchMoviesApi() {
        ResponseEntity<Map<String, Object>> result = moviesController.searchMoviesApi("test", null, null);
        
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
    }

    @Test
    @DisplayName("Should return error for invalid ID in API endpoint")
    public void testSearchMoviesApi_InvalidId() {
        ResponseEntity<Map<String, Object>> result = moviesController.searchMoviesApi(null, -1L, null);
        
        assertNotNull(result);
        assertEquals(400, result.getStatusCode().value());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.containsKey("error"));
    }

    @Test
    @DisplayName("Should return empty results for API search with no matches")
    public void testSearchMoviesApi_NoResults() {
        ResponseEntity<Map<String, Object>> result = moviesController.searchMoviesApi("NonExistentMovie", null, null);
        
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(0, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertTrue(movies.isEmpty());
    }

    @Test
    @DisplayName("Should integrate with movie service correctly")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(2, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        assertEquals("Action Movie", movies.get(1).getMovieName());
    }
}