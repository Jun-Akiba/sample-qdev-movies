# Movie Service API Documentation 🏴‍☠️

Ahoy matey! This be the complete API documentation for our movie treasure hunting service. All endpoints be tested and ready for yer adventures!

## Base URL
```
http://localhost:8080
```

## Endpoints Overview

| Method | Endpoint | Description | Response Type |
|--------|----------|-------------|---------------|
| GET | `/movies` | Get all movies with search form | HTML |
| GET | `/movies/{id}/details` | Get movie details | HTML |
| GET | `/movies/search` | Search movies (web interface) | HTML/JSON |
| GET | `/api/movies/search` | Search movies (API only) | JSON |

## Detailed Endpoint Documentation

### 1. Get All Movies
**Endpoint:** `GET /movies`

**Description:** Returns an HTML page displaying all movies in a grid layout with a search form.

**Response:** HTML page with movie cards and search interface

**Example:**
```bash
curl http://localhost:8080/movies
```

---

### 2. Get Movie Details
**Endpoint:** `GET /movies/{id}/details`

**Description:** Returns detailed information about a specific movie including reviews.

**Path Parameters:**
- `id` (required): Movie ID (1-12)

**Response:** HTML page with movie details

**Example:**
```bash
curl http://localhost:8080/movies/1/details
```

**Error Cases:**
- `404`: Movie not found (returns error page)

---

### 3. Search Movies (Web Interface)
**Endpoint:** `GET /movies/search`

**Description:** Search and filter movies with optional format selection.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | No | Movie name (partial match, case-insensitive) |
| `id` | Long | No | Specific movie ID (takes priority) |
| `genre` | String | No | Genre filter (partial match, case-insensitive) |
| `format` | String | No | Response format: "html" (default) or "json" |

**Response:** 
- HTML page with search results (format=html)
- JSON response (format=json)

**Examples:**

Search by name:
```bash
curl "http://localhost:8080/movies/search?name=prison"
```

Search by genre:
```bash
curl "http://localhost:8080/movies/search?genre=drama"
```

Search by ID:
```bash
curl "http://localhost:8080/movies/search?id=1"
```

Combined search:
```bash
curl "http://localhost:8080/movies/search?name=the&genre=action"
```

Get JSON response:
```bash
curl "http://localhost:8080/movies/search?name=prison&format=json"
```

**JSON Response Format:**
```json
{
  "success": true,
  "count": 1,
  "message": "Ahoy! Found 1 treasure(s) matching yer search!",
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

**Error Response (JSON):**
```json
{
  "success": false,
  "error": "Blimey! Invalid movie ID provided, matey!",
  "count": 0,
  "movies": []
}
```

---

### 4. Search Movies API (JSON Only)
**Endpoint:** `GET /api/movies/search`

**Description:** RESTful API endpoint that returns only JSON responses. Perfect for AJAX calls and external integrations.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | No | Movie name (partial match, case-insensitive) |
| `id` | Long | No | Specific movie ID (takes priority) |
| `genre` | String | No | Genre filter (partial match, case-insensitive) |

**Response:** Always JSON

**Examples:**

```bash
# Search by name
curl "http://localhost:8080/api/movies/search?name=prison"

# Search by genre
curl "http://localhost:8080/api/movies/search?genre=sci"

# Search by ID
curl "http://localhost:8080/api/movies/search?id=1"

# No parameters (returns all movies)
curl "http://localhost:8080/api/movies/search"
```

**Success Response:**
```json
{
  "success": true,
  "count": 2,
  "message": "Found 2 movies matching the search criteria",
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ]
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "error": "Invalid movie ID provided",
  "count": 0,
  "movies": []
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "error": "Internal server error occurred during search",
  "count": 0,
  "movies": []
}
```

## Search Logic & Behavior

### Filter Priority
1. **ID Search**: If `id` parameter is provided and valid, only that movie is returned (other parameters ignored)
2. **Name Filter**: Applied to movie names (case-insensitive, partial match)
3. **Genre Filter**: Applied to genres (case-insensitive, partial match)

### Input Validation
- **ID**: Must be positive integer, null/zero/negative values return empty results
- **Name**: Trimmed, empty/whitespace-only strings ignored
- **Genre**: Trimmed, empty/whitespace-only strings ignored

### Edge Cases
- **No parameters**: Returns all movies
- **No matches**: Returns empty array with appropriate message
- **Invalid ID**: Returns 400 error for API, error message for web interface
- **Server errors**: Returns 500 error with generic message

## HTTP Status Codes

| Code | Description | When |
|------|-------------|------|
| 200 | OK | Successful request |
| 400 | Bad Request | Invalid parameters (e.g., negative ID) |
| 404 | Not Found | Movie details for non-existent ID |
| 500 | Internal Server Error | Unexpected server error |

## Rate Limiting
Currently no rate limiting is implemented. In production, consider implementing:
- Request rate limiting per IP
- API key authentication for `/api/*` endpoints
- CORS configuration for cross-origin requests

## Testing the API

### Using curl
```bash
# Test all movies
curl -X GET http://localhost:8080/movies

# Test search with multiple parameters
curl -X GET "http://localhost:8080/api/movies/search?name=the&genre=drama"

# Test error handling
curl -X GET "http://localhost:8080/api/movies/search?id=-1"
```

### Using JavaScript (AJAX)
```javascript
// Search movies using fetch API
fetch('/api/movies/search?name=prison&genre=drama')
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      console.log(`Found ${data.count} movies:`, data.movies);
    } else {
      console.error('Search failed:', data.error);
    }
  })
  .catch(error => console.error('Network error:', error));
```

## Available Movie Data

The service includes 12 movies with the following genres:
- Drama
- Crime/Drama  
- Action/Crime
- Drama/Romance
- Action/Sci-Fi
- Adventure/Fantasy
- Adventure/Sci-Fi
- Drama/History
- Drama/Thriller

Movie IDs range from 1 to 12. Each movie includes:
- ID, name, director, year, genre, description, duration, IMDB rating, and icon

## Pirate Language Features 🏴‍☠️

The API includes fun pirate-themed elements:
- **Success messages**: "Ahoy! Found X treasure(s)!"
- **Error messages**: "Arrr! No movies found, matey!"
- **Code comments**: Pirate-themed documentation
- **UI elements**: Pirate emojis and nautical terminology

These add personality while maintaining professional API functionality!