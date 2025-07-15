package com.example.coursesearch.service;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.dto.SearchResponse;
import com.example.coursesearch.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.elasticsearch.uris=http://localhost:9200"
})
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();

        CourseDocument course1 = new CourseDocument();
        course1.setId("test1");
        course1.setTitle("Math Basics");
        course1.setDescription("Basic mathematics course");
        course1.setCategory("Math");
        course1.setType("COURSE");
        course1.setMinAge(8);
        course1.setMaxAge(12);
        course1.setPrice(100.0);
        course1.setNextSessionDate(LocalDate.now().plusDays(1)); // Use LocalDate here

        CourseDocument course2 = new CourseDocument();
        course2.setId("test2");
        course2.setTitle("Science Lab");
        course2.setDescription("Hands-on science experiments");
        course2.setCategory("Science");
        course2.setType("COURSE");
        course2.setMinAge(10);
        course2.setMaxAge(15);
        course2.setPrice(150.0);
        course2.setNextSessionDate(LocalDate.now().plusDays(2)); // Use LocalDate here

        courseRepository.saveAll(Arrays.asList(course1, course2));
    }

    @Test
    void testSearchByKeyword() {
        SearchResponse response = courseService.searchCourses("Math", null, null, null, null,
                null, null, null, null, 0, 10);

        assertEquals(1, response.getTotal());
        assertEquals("Math Basics", response.getCourses().get(0).getTitle());
    }

    @Test
    void testSearchByCategory() {
        SearchResponse response = courseService.searchCourses(null, null, null, "Science", null,
                null, null, null, null, 0, 10);

        assertEquals(1, response.getTotal());
        assertEquals("Science Lab", response.getCourses().get(0).getTitle());
    }

    @Test
    void testSearchByPriceRange() {
        SearchResponse response = courseService.searchCourses(null, null, null, null, null,
                90.0, 120.0, null, null, 0, 10);

        assertEquals(1, response.getTotal());
        assertEquals("Math Basics", response.getCourses().get(0).getTitle());
    }

    @Test
    void testSortByPrice() {
        SearchResponse response = courseService.searchCourses(null, null, null, null, null,
                null, null, null, "priceAsc", 0, 10);

        assertEquals(2, response.getTotal());
        assertEquals("Math Basics", response.getCourses().get(0).getTitle());
        assertEquals("Science Lab", response.getCourses().get(1).getTitle());
    }
}
