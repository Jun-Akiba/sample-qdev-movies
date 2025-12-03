package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy! Unit tests for the MovieService search functionality
 * Testing our treasure hunting capabilities, savvy?
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    public void testSearchMovies_NoFilters() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should find movie by exact ID")
    public void testSearchMovies_ByValidId() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return empty list for invalid ID")
    public void testSearchMovies_ByInvalidId() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for zero or negative ID")
    public void testSearchMovies_ByZeroOrNegativeId() {
        List<Movie> resultsZero = movieService.searchMovies(null, 0L, null);
        List<Movie> resultsNegative = movieService.searchMovies(null, -1L, null);
        
        assertNotNull(resultsZero);
        assertTrue(resultsZero.isEmpty());
        
        assertNotNull(resultsNegative);
        assertTrue(resultsNegative.isEmpty());
    }

    @Test
    @DisplayName("Should find movies by partial name match (case insensitive)")
    public void testSearchMovies_ByPartialName() {
        List<Movie> results = movieService.searchMovies("prison", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
        
        // Test case insensitive
        List<Movie> resultsUpperCase = movieService.searchMovies("PRISON", null, null);
        assertEquals(1, resultsUpperCase.size());
        assertEquals("The Prison Escape", resultsUpperCase.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should find movies by exact name match")
    public void testSearchMovies_ByExactName() {
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return empty list for non-existent movie name")
    public void testSearchMovies_ByNonExistentName() {
        List<Movie> results = movieService.searchMovies("Non Existent Movie", null, null);
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find movies by genre (case insensitive)")
    public void testSearchMovies_ByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Verify all results contain "Drama" in genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
        
        // Test case insensitive
        List<Movie> resultsUpperCase = movieService.searchMovies(null, null, "DRAMA");
        assertEquals(results.size(), resultsUpperCase.size());
    }

    @Test
    @DisplayName("Should find movies by partial genre match")
    public void testSearchMovies_ByPartialGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "sci");
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Verify all results contain "sci" in genre (case insensitive)
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("sci"));
        }
    }

    @Test
    @DisplayName("Should return empty list for non-existent genre")
    public void testSearchMovies_ByNonExistentGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "NonExistentGenre");
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should combine name and genre filters")
    public void testSearchMovies_CombinedNameAndGenre() {
        List<Movie> results = movieService.searchMovies("the", null, "drama");
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Verify all results match both criteria
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should handle empty string parameters")
    public void testSearchMovies_EmptyStrings() {
        List<Movie> results = movieService.searchMovies("", null, "");
        
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should handle whitespace-only parameters")
    public void testSearchMovies_WhitespaceOnly() {
        List<Movie> results = movieService.searchMovies("   ", null, "   ");
        
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should prioritize ID search over other parameters")
    public void testSearchMovies_IdTakesPriority() {
        // Search with ID should ignore name and genre parameters
        List<Movie> results = movieService.searchMovies("Different Movie", 1L, "Different Genre");
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return all unique genres")
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Check that genres are unique (no duplicates)
        assertEquals(genres.size(), genres.stream().distinct().count());
        
        // Check that genres are sorted
        List<String> sortedGenres = genres.stream().sorted().toList();
        assertEquals(sortedGenres, genres);
    }

    @Test
    @DisplayName("Should get movie by valid ID")
    public void testGetMovieById_ValidId() {
        Optional<Movie> result = movieService.getMovieById(1L);
        
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("The Prison Escape", result.get().getMovieName());
    }

    @Test
    @DisplayName("Should return empty for invalid ID")
    public void testGetMovieById_InvalidId() {
        Optional<Movie> result = movieService.getMovieById(999L);
        
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty for null or non-positive ID")
    public void testGetMovieById_NullOrNonPositiveId() {
        Optional<Movie> resultNull = movieService.getMovieById(null);
        Optional<Movie> resultZero = movieService.getMovieById(0L);
        Optional<Movie> resultNegative = movieService.getMovieById(-1L);
        
        assertFalse(resultNull.isPresent());
        assertFalse(resultZero.isPresent());
        assertFalse(resultNegative.isPresent());
    }

    @Test
    @DisplayName("Should return all movies")
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        
        // Verify we have the expected number of movies from the JSON file
        assertTrue(movies.size() >= 10); // We know there are at least 10 movies in the test data
    }
}