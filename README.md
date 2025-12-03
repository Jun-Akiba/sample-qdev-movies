# Movie Service - Spring Boot Demo Application 🏴‍☠️

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate-themed twist!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **🔍 Movie Search & Filtering**: Search movies by name, ID, or genre with pirate-themed interface
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **🌐 REST API**: JSON endpoints for programmatic access to movie data
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **⚓ Pirate Language**: Fun pirate-themed messages and interface elements

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating
- **JSON** for data processing

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Movie Search**: http://localhost:8080/movies/search?name=prison&genre=drama

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic for movie operations
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie catalog data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── static/css/                       # CSS stylesheets
│       └── templates/                        # Thymeleaf HTML templates
└── test/                                     # Unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/
            └── movies/
                ├── MovieServiceTest.java     # Service layer tests
                ├── MoviesControllerTest.java # Controller tests
                └── MovieTest.java            # Model tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information. Includes a search form for filtering movies.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### 🔍 Search Movies (Web Interface)
```
GET /movies/search
```
Search and filter movies with an interactive web form. Returns HTML page with search results.

**Query Parameters:**
- `name` (optional): Movie name to search for (partial matches, case-insensitive)
- `id` (optional): Specific movie ID to find
- `genre` (optional): Genre to filter by (partial matches, case-insensitive)
- `format` (optional): Response format - "html" (default) or "json"

**Examples:**
```bash
# Search by movie name
http://localhost:8080/movies/search?name=prison

# Search by genre
http://localhost:8080/movies/search?genre=drama

# Search by specific ID
http://localhost:8080/movies/search?id=1

# Combined search (name + genre)
http://localhost:8080/movies/search?name=the&genre=action

# Get JSON response
http://localhost:8080/movies/search?name=prison&format=json
```

### 🌐 Search Movies API (JSON Only)
```
GET /api/movies/search
```
RESTful API endpoint that returns only JSON responses. Perfect for AJAX calls and external integrations.

**Query Parameters:**
- `name` (optional): Movie name to search for
- `id` (optional): Specific movie ID to find  
- `genre` (optional): Genre to filter by

**Response Format:**
```json
{
  "success": true,
  "count": 1,
  "message": "Found 1 movies matching the search criteria",
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ]
}
```

**Error Response:**
```json
{
  "success": false,
  "error": "Invalid movie ID provided",
  "count": 0,
  "movies": []
}
```

## Search Features 🏴‍☠️

### Search Capabilities
- **Name Search**: Partial, case-insensitive matching
- **ID Search**: Exact match (takes priority over other filters)
- **Genre Search**: Partial, case-insensitive matching
- **Combined Filters**: Mix name and genre for precise results
- **Empty Results Handling**: Friendly pirate-themed messages

### Edge Cases Handled
- Invalid or negative movie IDs
- Empty search parameters
- Whitespace-only input
- Non-existent movies or genres
- Server errors with graceful fallback

### Pirate Language Features
- Search form titled "🏴‍☠️ Search for Cinematic Treasures"
- Success messages: "Ahoy! Found X cinematic treasure(s)!"
- Error messages: "Arrr! No movies found matching yer search criteria, matey!"
- Button text: "🔍 Search Treasures" and "⚓ Show All Movies"

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new features like advanced filtering or sorting
- Improve the responsive design
- Add more search capabilities (by director, year, rating)
- Implement user authentication and favorites

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest

# Run tests with coverage
mvn test jacoco:report
```

### Code Quality

The project follows Java best practices:
- **Service Layer**: Business logic separated from controllers
- **Error Handling**: Comprehensive exception handling with user-friendly messages
- **Input Validation**: All user inputs are validated and sanitized
- **Unit Testing**: Comprehensive test coverage for all major functionality
- **Documentation**: JavaDoc comments and detailed README

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.
