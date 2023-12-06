package ru.yandex.practicum.filmorate.service.film;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    private final FeedStorage feedStorage;

    public Film like(Film film, int userId) {
        return likeStorage.like(film, userId);
    }

    public Film deleteLike(Film film, int userId) {
        Film deletedLike = filmStorage.deleteLike(film, userId);
        feedStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.LIKE)
                .operation(OperationType.REMOVE)
                .entityId(film.getId())
                .build());
        return deletedLike;
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getRating(count);
    }

    public Film create(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film update(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.findAllFilms();
    }

    public Film getFilm(@PathVariable int id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilm(int filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy) {
        return filmStorage.filmsByDirector(directorId, sortBy);
    }
}
