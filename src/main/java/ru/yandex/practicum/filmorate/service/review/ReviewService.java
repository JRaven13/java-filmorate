package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.likeReview.LikeReviewStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final LikeReviewStorage likeReviewStorage;
    private final FeedStorage feedStorage;

    public Review create(Review review) {
        Review createdReview = reviewStorage.create(review);
        feedStorage.addEvent(Event.builder()
                .userId(createdReview.getUserId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.REVIEW)
                .operation(OperationType.ADD)
                .entityId(createdReview.getReviewId())
                .build());
        return createdReview;
    }

    public Review update(Review review) {
        Review updatedReview = reviewStorage.update(review).orElseThrow(() -> new NotFoundException("Отзыв не найден."));
        feedStorage.addEvent(Event.builder()
                .userId(updatedReview.getUserId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.REVIEW)
                .operation(OperationType.UPDATE)
                .entityId(updatedReview.getReviewId())
                .build());
        return updatedReview;
    }

    public void delete(Integer id) {
        Optional<Review> review = reviewStorage.findById(id);
        if (review.isPresent()) {
            reviewStorage.delete(id);
            feedStorage.addEvent(Event.builder()
                    .userId(review.get().getUserId())
                    .timestamp(Instant.now().toEpochMilli())
                    .eventType(EventType.REVIEW)
                    .operation(OperationType.REMOVE)
                    .entityId(review.get().getReviewId())
                    .build());
        }
    }

    public Review findById(Integer id) {
        return reviewStorage.findById(id).orElseThrow(() -> new NotFoundException("Отзыв не найден."));
    }

    public List<Review> findAll(Integer filmId, Integer count) {
        return reviewStorage.findAll(filmId, count);
    }

    public void createLike(Integer id, Integer userId) {
        likeReviewStorage.createLike(id, userId);
    }

    public void createDislike(Integer id, Integer userId) {
        likeReviewStorage.createDislike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        likeReviewStorage.deleteLike(id, userId);
    }

    public void deleteDislike(Integer id, Integer userId) {
        likeReviewStorage.deleteDislike(id, userId);
    }
}