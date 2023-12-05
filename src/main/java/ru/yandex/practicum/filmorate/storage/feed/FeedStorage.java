package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {

    public List<Event> getFeedByUserId(int userId);

    public void addEvent(Event event);
}
