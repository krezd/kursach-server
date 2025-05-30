package com.example.kursachserver.controller;

import com.example.kursachserver.dto.response.UserRatingDto;
import com.example.kursachserver.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<UserRatingDto> getRatings(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start = now.minus(30, ChronoUnit.DAYS);
        OffsetDateTime end = now;
        if (from != null && !from.isEmpty()) {
            start = OffsetDateTime.parse(from);
        }
        if (to != null && !to.isEmpty()) {
            end = OffsetDateTime.parse(to);
        }
        return ratingService.getUserRatings(start, end);
    }

    @GetMapping("/user/{id}")
    public UserRatingDto getRatingForUser(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start = now.minus(30, ChronoUnit.DAYS);
        OffsetDateTime end = now;
        if (from != null && !from.isEmpty()) {
            start = OffsetDateTime.parse(from);
        }
        if (to != null && !to.isEmpty()) {
            end = OffsetDateTime.parse(to);
        }
        return ratingService.getUserRating(id, start, end);
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> downloadRatingsReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start = now.minus(30, ChronoUnit.DAYS);
        OffsetDateTime end = now;
        if (from != null && !from.isEmpty()) {
            start = OffsetDateTime.parse(from);
        }
        if (to != null && !to.isEmpty()) {
            end = OffsetDateTime.parse(to);
        }
        byte[] pdf = ratingService.generateRatingReport(start, end);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rating-report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/report/{id}")
    public ResponseEntity<byte[]> downloadRatingsReport(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start = now.minus(30, ChronoUnit.DAYS);
        OffsetDateTime end = now;
        if (from != null && !from.isEmpty()) {
            start = OffsetDateTime.parse(from);
        }
        if (to != null && !to.isEmpty()) {
            end = OffsetDateTime.parse(to);
        }
        byte[] pdf = ratingService.generateUserRatingReport(id, start, end);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rating-report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


}
