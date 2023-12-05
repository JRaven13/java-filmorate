package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("Поступил запрос на добавление отзыва: {}", review);
        return service.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Поступил запрос на обновление отзыва: {}", review);
        return service.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Поступил запрос на удаление отзыва: {}", id);
        service.delete(id);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Integer id) {
        log.info("Поступил запрос на получение отзыва: {}", id);
        return service.findById(id);
    }

    @GetMapping()
    public List<Review> findAll(@RequestParam(defaultValue = "0", required = false) @Positive Integer filmId, @RequestParam(defaultValue = "10", required = false) @Positive Integer count) {
        log.info("Поступил запрос на получение всех отзывов");
        return service.findAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void createLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Поступил запрос от пользователя {} на создание like к отзыву {}", userId, id);
        service.createLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void createDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Поступил запрос от пользователя {} на создание dislike к отзыву {}", userId, id);
        service.createDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Поступил запрос от пользователя {} на удаление like к отзыву {}", userId, id);
        service.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Поступил запрос от пользователя {} на удаление dislike к отзыву {}", userId, id);
        service.deleteDislike(id, userId);
    }
}

