package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface LikeStorage {

    /**
     * Метод для получения лайков у фильма по его id
     */
    Set<Integer> getLikesForCurrentFilm(int id);

    public Film like(Film film, int userId);

    public Film deleteLike(Film film, int userId);

}
