package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/films", produces = "application/json")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на добавление фильма.");
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film update(@Validated(Update.class) @RequestBody final Film film) {
        log.info("Updating film {}", film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film like(@PathVariable int id, @PathVariable int userId) {
        log.info("Поступил запрос на присвоение лайка фильму.");
       return filmService.like(id, userId);
    }

    @GetMapping()
    public List<Film> getFilms() {
        log.info("Поступил запрос на получение списка всех фильмов.");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Получен GET-запрос на получение фильма");
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(defaultValue = "10") String count) {
        log.info("Поступил запрос на получение списка популярных фильмов.");
        return filmService.getTopFilms(Integer.parseInt(count));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Поступил запрос на удаление лайка у фильма.");
        return filmService.deleteLike(userId, id);
    }

}
