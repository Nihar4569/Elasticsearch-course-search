package com.example.coursesearch.config;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.service.CourseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {

    private final CourseService courseService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataLoader(CourseService courseService) {
        this.courseService = courseService;
    }

    @Override
    public void run(String... args) throws Exception {
        courseService.deleteAll();
        loadSampleData();
    }

    private void loadSampleData() throws IOException {
        objectMapper.registerModule(new JavaTimeModule());

        ClassPathResource resource = new ClassPathResource("sample-courses.json");
        List<Map<String, Object>> rawCourses = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        List<CourseDocument> courses = rawCourses.stream()
                .map(this::mapToCourseDocument)
                .toList();

        courseService.saveAll(courses);
        System.out.println("Loaded " + courses.size() + " courses into Elasticsearch");
    }

    private CourseDocument mapToCourseDocument(Map<String, Object> rawCourse) {
        CourseDocument course = new CourseDocument();

        course.setId((String) rawCourse.get("id"));
        course.setTitle((String) rawCourse.get("title"));
        course.setDescription((String) rawCourse.get("description"));
        course.setCategory((String) rawCourse.get("category"));
        course.setType((String) rawCourse.get("type"));
        course.setGradeRange((String) rawCourse.get("gradeRange"));

        Object minAgeObj = rawCourse.get("minAge");
        if (minAgeObj instanceof Number) {
            course.setMinAge(((Number) minAgeObj).intValue());
        }

        Object maxAgeObj = rawCourse.get("maxAge");
        if (maxAgeObj instanceof Number) {
            course.setMaxAge(((Number) maxAgeObj).intValue());
        }

        Object priceObj = rawCourse.get("price");
        if (priceObj instanceof Number) {
            course.setPrice(((Number) priceObj).doubleValue());
        }

        String dateString = (String) rawCourse.get("nextSessionDate");
        if (dateString != null) {
            // Parse ISO 8601 with timezone then convert to LocalDate (date only)
            LocalDate date = OffsetDateTime.parse(dateString).toLocalDate();
            course.setNextSessionDate(date);
        }

        return course;
    }
}
