package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int id = 0;
    private final Comparator<Film> filmComparator = (film1, film2) -> {
        if (film1.getLikes().size() == film2.getLikes().size()) {
            return (film1.getId() - film2.getId());
        } else {
            return film1.getLikes().size() - film2.getLikes().size();
        }
    };

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Collection<Film> getAll() {
        Collection<Film> users = inMemoryFilmStorage.getAllFilms();
        log.debug("Количество фильмов {}", users.size());
        return users;
    }

    public Film getFilmById(int id) {
        if (!inMemoryFilmStorage.existById(id)) {
            throw new ObjectNotFoundException("Такого фильма нет!");
        }
        return inMemoryFilmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.debug("Дата релиза раньше 28.12.1985 г. Введено: {}", film.getReleaseDate());
            throw new ValidateException("Дата релиза должна быть не раньше не раньше 28 декабря 1895 года");
        }
        if (film.getId() == 0) {
            film.setId(generateID());
        }
        inMemoryFilmStorage.addFilm(film);
        log.info("Фильм добавлен {}", film);
        return film;
    }

    public Film update(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.debug("Дата релиза раньше 28.12.1985 г. Введено {}", film.getReleaseDate());
            throw new ValidateException("Дата релиза должна быть не раньше не раньше 28 декабря 1895 года");
        }
        if (!inMemoryFilmStorage.existById(film.getId())) {
            throw new ObjectNotFoundException("Такого фильма нет!");
        }
        inMemoryFilmStorage.updateFilm(film);
        log.info("Фильм изменён {}", film);
        return film;
    }

    public Film addLike(int id, int userId) {
        if (!inMemoryFilmStorage.existById(id)) {
            throw new ObjectNotFoundException("Такого фильма нет!");
        }
        return inMemoryFilmStorage.addLike(id, userId);
    }

    public Film deleteLike(int id, int userID) {
        if (!inMemoryFilmStorage.existById(id)) {
            throw new ObjectNotFoundException("Такого фильма нет!");
        }
        if (userID < 0) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        return inMemoryFilmStorage.deleteLike(id, userID);
    }

    public Collection<Film> topFilmsWithCount(int count) {
        Set<Film> popularFilms = new TreeSet<>(filmComparator.reversed());
        Collection<Film> films = inMemoryFilmStorage.getAllFilms();
        popularFilms.addAll(films);
        return popularFilms.stream().limit(count).collect(Collectors.toSet());
    }

    private int generateID() {
        return ++this.id;
    }

}