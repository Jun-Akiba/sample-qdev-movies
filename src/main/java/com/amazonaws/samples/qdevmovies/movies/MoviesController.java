package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy there! This be the search endpoint for finding movies in our treasure chest!
     * Accepts query parameters for name, id, and genre to filter the results.
     * Returns JSON response for API calls or HTML for browser requests.
     * 
     * @param name Movie name to search for (optional)
     * @param id Movie ID to search for (optional)
     * @param genre Genre to filter by (optional)
     * @param format Response format: "json" for API, "html" for web page (default: html)
     * @param model Spring model for HTML responses
     * @return JSON response or HTML template name
     */
    @GetMapping("/movies/search")
    public Object searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "format", defaultValue = "html") String format,
            org.springframework.ui.Model model) {
        
        logger.info("Arrr! Searching for movies with name: {}, id: {}, genre: {}, format: {}", 
                   name, id, genre, format);
        
        try {
            // Validate input parameters
            if (id != null && id <= 0) {
                logger.warn("Invalid movie ID provided: {}", id);
                if ("json".equalsIgnoreCase(format)) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Blimey! Invalid movie ID provided, matey!"));
                } else {
                    model.addAttribute("error", "Blimey! Invalid movie ID provided, matey!");
                    model.addAttribute("movies", movieService.getAllMovies());
                    model.addAttribute("genres", movieService.getAllGenres());
                    return "movies";
                }
            }
            
            // Perform the search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Handle JSON response for API calls
            if ("json".equalsIgnoreCase(format)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("count", searchResults.size());
                response.put("movies", searchResults);
                
                if (searchResults.isEmpty()) {
                    response.put("message", "Arrr! No movies found matching yer search criteria, matey!");
                } else {
                    response.put("message", String.format("Ahoy! Found %d treasure(s) matching yer search!", searchResults.size()));
                }
                
                return ResponseEntity.ok(response);
            }
            
            // Handle HTML response for web interface
            model.addAttribute("movies", searchResults);
            model.addAttribute("genres", movieService.getAllGenres());
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            
            if (searchResults.isEmpty()) {
                model.addAttribute("message", "Arrr! No movies found matching yer search criteria, matey! Try different search terms.");
            } else {
                model.addAttribute("message", String.format("Ahoy! Found %d cinematic treasure(s) matching yer search!", searchResults.size()));
            }
            
            return "movies";
            
        } catch (Exception e) {
            logger.error("Shiver me timbers! Error occurred during movie search: {}", e.getMessage(), e);
            
            if ("json".equalsIgnoreCase(format)) {
                return ResponseEntity.internalServerError().body(createErrorResponse("Shiver me timbers! Something went wrong during the search!"));
            } else {
                model.addAttribute("error", "Shiver me timbers! Something went wrong during the search!");
                model.addAttribute("movies", movieService.getAllMovies());
                model.addAttribute("genres", movieService.getAllGenres());
                return "movies";
            }
        }
    }

    /**
     * API endpoint that returns only JSON responses for movie search
     * Perfect for AJAX calls and external integrations, savvy?
     */
    @GetMapping("/api/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesApi(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("API search request - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Validate input parameters
            if (id != null && id <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid movie ID provided"));
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", searchResults.size());
            response.put("movies", searchResults);
            
            if (searchResults.isEmpty()) {
                response.put("message", "No movies found matching the search criteria");
            } else {
                response.put("message", String.format("Found %d movies matching the search criteria", searchResults.size()));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error occurred during API movie search: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Internal server error occurred during search"));
        }
    }

    /**
     * Helper method to create consistent error responses
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);
        errorResponse.put("count", 0);
        errorResponse.put("movies", List.of());
        return errorResponse;
    }
}