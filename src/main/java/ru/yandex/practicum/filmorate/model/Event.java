package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private int eventId;
    private int entityId;
    private int userId;
    private long timestamp;
    private EventType eventType;
    private OperationType operation;
}
