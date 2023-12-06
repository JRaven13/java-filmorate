package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.List;


public interface FilmStorage {
    /**
     * Метод для получения списка фильмов
     */
    List<Film> findAllFilms();

    /**
     * Метод для добавления фильма
     */
    Film addFilm(Film film);

    /**
     * Метод для обновления фильма
     */
    Film updateFilm(Film film);

    /**
     * Метод получения фильма по id
     */
    Film getFilmById(int id);

    /**
     * Метод для присвоения лайка фильму
     */
    Film like(Film film, int userId);

    /**
     * Метод для удаления лайка с фильма
     */
    Film deleteLike(Film film, int userId);

    /**
     * Метод для получения списка популярных фильмов
     */
    List<Film> getRating(int count);

    /**
     * Метод для удаления фильма
     */
    void deleteFilm(int filmId);

    /**
     * Метод для получения списка фильмов по режиссеру
     */
    LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy);

    List<Film> searchBy(String query, String by);
}
