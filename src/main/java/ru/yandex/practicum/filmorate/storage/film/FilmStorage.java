package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    Film like(Film film, int userId);

    Film deleteLike(Film film, int userId);

    List<Film> getRating(int count);

    List<Film> getCommonFilms(int userId, int friendId);
}
