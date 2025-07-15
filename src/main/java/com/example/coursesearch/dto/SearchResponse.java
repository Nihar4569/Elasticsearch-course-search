package com.example.coursesearch.dto;

import com.example.coursesearch.document.CourseDocument;

import java.util.List;
import java.util.Objects;

public class SearchResponse {
    private long total;
    private List<CourseDocument> courses;

    public SearchResponse() {
    }

    public SearchResponse(long total, List<CourseDocument> courses) {
        this.total = total;
        this.courses = courses;
    }

    public long getTotal() {
        return total;
    }

    public List<CourseDocument> getCourses() {
        return courses;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setCourses(List<CourseDocument> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "total=" + total +
                ", courses=" + courses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchResponse)) return false;
        SearchResponse that = (SearchResponse) o;
        return total == that.total &&
                Objects.equals(courses, that.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, courses);
    }
}
