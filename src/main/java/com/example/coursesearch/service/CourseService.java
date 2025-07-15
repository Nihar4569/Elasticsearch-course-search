package com.example.coursesearch.service;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.dto.SearchResponse;
import com.example.coursesearch.repository.CourseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public CourseService(CourseRepository courseRepository, ElasticsearchOperations elasticsearchOperations) {
        this.courseRepository = courseRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public void saveAll(List<CourseDocument> courses) {
        courseRepository.saveAll(courses);
    }

    public void deleteAll() {
        courseRepository.deleteAll();
    }

    public SearchResponse searchCourses(String query, Integer minAge, Integer maxAge,
                                        String category, String type, Double minPrice,
                                        Double maxPrice, LocalDateTime startDate,
                                        String sort, int page, int size) {

        Criteria criteria = new Criteria();

        if (query != null && !query.isEmpty()) {
            Criteria titleCriteria = new Criteria("title").matches(query);
            Criteria descriptionCriteria = new Criteria("description").matches(query);
            criteria = criteria.and(titleCriteria.or(descriptionCriteria));
        }

        // Fixed age filtering logic here:
        if (minAge != null) {
            // Course maxAge should be >= minAge filter (course covers this age)
            criteria = criteria.and(new Criteria("maxAge").greaterThanEqual(minAge));
        }

        if (maxAge != null) {
            // Course minAge should be <= maxAge filter (course covers this age)
            criteria = criteria.and(new Criteria("minAge").lessThanEqual(maxAge));
        }

        if (category != null && !category.isEmpty()) {
            criteria = criteria.and(new Criteria("category").is(category));
        }

        if (type != null && !type.isEmpty()) {
            criteria = criteria.and(new Criteria("type").is(type));
        }

        if (minPrice != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(minPrice));
        }

        if (maxPrice != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(maxPrice));
        }

        if (startDate != null) {
            criteria = criteria.and(new Criteria("nextSessionDate").greaterThanEqual(startDate));
        }

        Sort.Direction sortDirection = Sort.Direction.ASC;
        String sortField = "nextSessionDate";

        if (sort != null) {
            switch (sort) {
                case "priceAsc":
                    sortField = "price";
                    sortDirection = Sort.Direction.ASC;
                    break;
                case "priceDesc":
                    sortField = "price";
                    sortDirection = Sort.Direction.DESC;
                    break;
                default:
                    sortField = "nextSessionDate";
                    sortDirection = Sort.Direction.ASC;
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        Query searchQuery = new CriteriaQuery(criteria).setPageable(pageable);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        List<CourseDocument> courses = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new SearchResponse(searchHits.getTotalHits(), courses);
    }

    public List<String> getSuggestions(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        Criteria criteria = new Criteria("title").startsWith(query);
        Query searchQuery = new CriteriaQuery(criteria);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(CourseDocument::getTitle)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<String> getFuzzySearchSuggestions(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        Criteria criteria = new Criteria("title").contains(query);
        Query searchQuery = new CriteriaQuery(criteria);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(CourseDocument::getTitle)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }
}
