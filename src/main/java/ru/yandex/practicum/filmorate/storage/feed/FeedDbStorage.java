package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeedByUserId(int userId) {
        String sqlQuery = "SELECT * FROM feed WHERE user_id = ?";
        return new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToEvent, userId));
    }

    @Override
    public void addEvent(Event event) {
        String sqlQuery = "INSERT INTO feed (user_id, timestamp, event_type, operation, entity_id) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, event.getUserId(), event.getTimestamp(), event.getEventType().toString(),
                event.getOperation().toString(), event.getEntityId());
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getInt("event_id"))
                .userId(resultSet.getInt("user_id"))
                .timestamp(resultSet.getLong("timestamp"))
                .eventType(EventType.valueOf(resultSet.getString("event_type")))
                .operation(OperationType.valueOf(resultSet.getString("operation")))
                .entityId(resultSet.getInt("entity_id"))
                .build();
    }
}
