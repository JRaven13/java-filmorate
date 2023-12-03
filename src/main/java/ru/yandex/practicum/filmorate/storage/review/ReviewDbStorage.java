package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private JdbcTemplate template;

    @Autowired
    public ReviewDbStorage(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Review addReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(template)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(toMap(review)).intValue());

        log.info("Поступил запрос на добавление отзыва. Отзыв добавлен добавлен.");
        return review;
    }

    @Override
    public Collection<Review> getAll() {
        return template.query("SELECT * FROM REVIEWS", this::mapRowToReview);
    }


    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .build();

    }

    private Map<String, Object> toMap(Review review) {
        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        return values;
    }
}