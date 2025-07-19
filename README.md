# Course Search API

A Spring Boot application that provides course search functionality using Elasticsearch with support for filtering, sorting, pagination, autocomplete, and fuzzy search.

## Features

  - Full-text search on course titles and descriptions
  - Multiple filters (age, category, type, price, date)
  - Sorting by date and price
  - Pagination support
  
  - Autocomplete suggestions for course titles
  - Partial match support for autocomplete (no real fuzzy/typo correction)

## Prerequisites

- Java 17+
- Docker and Docker Compose
- Maven 3.6+

## Setup Instructions

### 1. Start Elasticsearch

```bash
docker-compose up -d
```

Verify Elasticsearch is running:
```bash
curl http://localhost:9200
```

### 2. Build and Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

The application will automatically:
- Connect to Elasticsearch on localhost:9200
- Create the courses index
- Load sample data from `sample-courses.json`

### 3. Verify Data Loading

Check that courses are indexed:
```bash
curl "http://localhost:9200/courses/_count"
```

## API Endpoints

### Search Courses

**Endpoint**: `GET /api/search`

**Parameters**:
- `q` - Search keyword (searches title and description)
- `minAge` - Minimum age filter
- `maxAge` - Maximum age filter
- `category` - Category filter (Math, Science, Art, etc.)
- `type` - Course type filter (COURSE, CLUB, ONE_TIME)
- `minPrice` - Minimum price filter
- `maxPrice` - Maximum price filter
- `startDate` - Start date filter (ISO-8601 format)
- `sort` - Sort order: `upcoming` (default), `priceAsc`, `priceDesc`
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)

**Examples**:

Search for math courses:
```bash
curl "http://localhost:8080/api/search?q=math"
```

Filter by category and age:
```bash
curl "http://localhost:8080/api/search?category=Science&minAge=10&maxAge=15"
```

Filter by price range:
```bash
curl "http://localhost:8080/api/search?minPrice=100&maxPrice=200"
```

Sort by price (ascending):
```bash
curl "http://localhost:8080/api/search?sort=priceAsc"
```

Sort by price (descending):
```bash
curl "http://localhost:8080/api/search?sort=priceDesc"
```

Filter by date (courses starting after specific date):
```bash
curl "http://localhost:8080/api/search?startDate=2025-07-01T00:00:00"
```

Pagination:
```bash
curl "http://localhost:8080/api/search?page=1&size=5"
```

Combined filters:
```bash
curl "http://localhost:8080/api/search?q=programming&category=Technology&minAge=12&maxAge=18&minPrice=150&sort=priceAsc&page=0&size=10"
```

### Autocomplete Suggestions

**Endpoint**: `GET /api/search/suggest`

**Parameters**:
- `q` - Partial title to get suggestions for

**Examples**:

Get suggestions for "phy":
```bash
curl "http://localhost:8080/api/search/suggest?q=phy"
```

Get suggestions for "math":
```bash
curl "http://localhost:8080/api/search/suggest?q=math"
```

## Similar to Fuzzy Search

The search supports **partial word matching**, which can feel similar to fuzzy search — but it does **not correct typos**.

**Examples**:

Search with partial title "math" (matches "Mathematics"):
```bash
curl "http://localhost:8080/api/search?q=math"
```

Search with partial title "chem" (matches "Chemistry"):
```bash
curl "http://localhost:8080/api/search?q=chem"
```

## Response Format

### Search Response
```json
{
  "total": 25,
  "courses": [
    {
      "id": "1",
      "title": "Introduction to Mathematics",
      "description": "Basic mathematics concepts...",
      "category": "Math",
      "type": "COURSE",
      "gradeRange": "1st-3rd",
      "minAge": 6,
      "maxAge": 9,
      "price": 120.50,
      "nextSessionDate": "2025-06-10T15:00:00"
    }
  ]
}
```

### Suggestions Response
```json
[
  "Advanced Physics",
  "Introduction to Physics",
  "Physics Lab"
]
```

## Running Tests

```bash
mvn test
```

The tests include:
- Search by keyword
- Filter by category
- Filter by price range
- Sort by price
- Pagination

## Project Structure

```
src/
├── main/
│   ├── java/com/example/coursesearch/
│   │   ├── CourseSearchApplication.java
│   │   ├── config/
│   │   │   ├── DataLoader.java
│   │   │   └── ElasticsearchConfig.java
│   │   ├── controller/
│   │   │   └── CourseController.java
│   │   ├── document/
│   │   │   └── CourseDocument.java
│   │   ├── dto/
│   │   │   └── SearchResponse.java
│   │   ├── repository/
│   │   │   └── CourseRepository.java
│   │   └── service/
│   │       └── CourseService.java
│   └── resources/
│       ├── application.properties
│       └── sample-courses.json
└── test/
    └── java/com/example/coursesearch/service/
        └── CourseServiceTest.java
```

## Key Components

- **CourseDocument**: Elasticsearch document mapping for course data
- **CourseRepository**: Spring Data Elasticsearch repository
- **CourseService**: Business logic for search operations
- **CourseController**: REST API endpoints
- **DataLoader**: Loads sample data on application startup
- **ElasticsearchConfig**: Elasticsearch connection configuration

## Troubleshooting

1. **Elasticsearch not starting**: Make sure Docker is running and port 9200 is available
2. **Connection refused**: Verify Elasticsearch is accessible at localhost:9200
3. **Data not loading**: Check application logs for any errors during startup
4. **Empty search results**: Ensure data was loaded properly by checking the count endpoint

## Sample Data

The application includes 50 sample courses with varied:
- Categories: Math, Science, Art, Technology, Sports, Music, Language, etc.
- Types: COURSE, CLUB, ONE_TIME
- Age ranges: 5-18 years
- Prices: $35-$300
- Session dates: Spanning multiple weeks

## Additional Features

- **Autocomplete**: Provides partial matches for course titles
- **Autocomplete**: Provides title suggestions as you type
- **Multiple Filters**: Combine multiple filters for precise searches
- **Flexible Sorting**: Sort by date or price in ascending/descending order
- **Pagination**: Navigate through large result sets efficiently
