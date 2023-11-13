package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    void addFilm(Film film);

    void deleteFilm(int id);

    void updateFilm(Film film);

    Collection<Film> getAllFilms();

    Film getFilmById(int id);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    boolean existById(int id);
}