package ru.yandex.practicum.filmorate.service.film;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    private final FeedStorage feedStorage;

    public Film like(Film film, int userId) {
        Film createdLike = likeStorage.like(film, userId);
        feedStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.LIKE)
                .operation(OperationType.ADD)
                .entityId(film.getId())
                .build());
        return createdLike;
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

    public List<Film> getTopFilms(int count, Optional<Integer> genreId, Optional<Integer> year) {
        return filmStorage.getRating(count, genreId, year);
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

    public Film getFilm(int id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilm(int filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy) {
        return filmStorage.filmsByDirector(directorId, sortBy);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getSearchResults(String query, String by) {
        return filmStorage.searchBy(query, by);
    }
}
