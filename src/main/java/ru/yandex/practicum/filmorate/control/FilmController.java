package ru.yandex.practicum.filmorate.control;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate DATE = LocalDate.of(1895, 12, 28);
    private int id = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if(film.getReleaseDate().isBefore(DATE)) {
            throw new InvalidEmailException("Фильм с датой релиза до 28.12.1895 года, добавить нельзя!");
        }
        if (film.getId() == 0) {
            film.setId(generateid());
        }
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new InvalidEmailException("Такого фильма нет!");
        }else {
            films.put(film.getId(), film);
        }
        return film;
    }

    private int generateid() {
        return ++id;
    }
}
