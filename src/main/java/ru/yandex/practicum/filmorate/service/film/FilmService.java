package ru.yandex.practicum.filmorate.service.film;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.Update;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;


    public Film like(int filmId, int userId) {
        return filmStorage.like(filmId, userId);
    }

    public Film deleteLike(int filmId, int userId) {
           return filmStorage.deleteLike(filmId, userId);
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

}
