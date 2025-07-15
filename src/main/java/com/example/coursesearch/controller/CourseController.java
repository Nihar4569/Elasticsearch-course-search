package com.example.coursesearch.controller;

import com.example.coursesearch.dto.SearchResponse;
import com.example.coursesearch.service.CourseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        SearchResponse response = courseService.searchCourses(
                q, minAge, maxAge, category, type, minPrice, maxPrice,
                startDate, sort, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/suggest")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String q) {
        List<String> suggestions = courseService.getSuggestions(q);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/search/fuzzy")
    public ResponseEntity<List<String>> getFuzzySuggestions(@RequestParam String q) {
        List<String> suggestions = courseService.getFuzzySearchSuggestions(q);
        return ResponseEntity.ok(suggestions);
    }
}
