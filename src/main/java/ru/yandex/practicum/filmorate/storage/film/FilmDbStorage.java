package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final LikeStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Override
    public List<Film> findAllFilms() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT film_id, name, description, release_date," +
                " duration, rating_mpa_id FROM films");
        return jdbcTemplate.query("SELECT * FROM films", this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue());
        mpaDbStorage.addMpaToFilm(film);
        genreDbStorage.addGenreNameToFilm(film);
        genreDbStorage.addGenresForCurrentFilm(film);
        log.info("Поступил запрос на добавление фильма. Фильм добавлен.");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name=?, description=?, release_date=?, duration=?, rating_mpa_id=? WHERE film_id=?";
        int rowsCount = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        mpaDbStorage.addMpaToFilm(film);
        genreDbStorage.updateGenresForCurrentFilm(film);
        genreDbStorage.addGenreNameToFilm(film);
        film.setGenres(genreDbStorage.getGenreForCurrentFilm(film.getId()));
        if (rowsCount > 0) {
            return film;
        }
        throw new NotFoundException("Фильм не найден.");
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT film_id, name, description, release_date, duration, rating_mpa_id " +
                "FROM films WHERE film_id=?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Фильм не найден.");
        }
    }

    @Override
    public Film like(Film film, int userId) {
        return likeDbStorage.like(film, userId);
    }

    @Override
    public Film deleteLike(Film film, int userId) {
        return likeDbStorage.deleteLike(film, userId);
    }

    @Override
    public List<Film> getRating(int count) {
        String sqlQuery = "SELECT films.*, COUNT(l.film_id) as count FROM films\n" +
                "LEFT JOIN likes l ON films.film_id=l.film_id\n" +
                "GROUP BY films.film_id\n" +
                "ORDER BY count DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaDbStorage.getMpa(resultSet.getInt("rating_mpa_id")))
                .build();
        film.setGenres(genreDbStorage.getGenreForCurrentFilm(film.getId()));
        return film;
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating_mpa_id", film.getMpa().getId());
        return values;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sqlQuery = "SELECT f.*, " +
                "COUNT(l3.film_id) FROM films AS f " +
                "LEFT JOIN likes AS l1 ON f.film_id = l1.film_id " +
                "LEFT JOIN users AS u1 ON l1.user_id = u1.user_id " +
                "LEFT JOIN likes AS l2 ON l1.film_id = l2.film_id " +
                "LEFT JOIN users AS u2 ON l2.user_id = u2.user_id " +
                "LEFT JOIN likes AS l3 ON f.film_id = l3.film_id " +
                "WHERE u1.user_id = ? AND u2.user_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l3.film_id) DESC, f.film_id";

        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(Objects.requireNonNull(resultSet.getDate("release_date")).toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(getMpaById(resultSet.getInt("rating_mpa_id")))
                .genres(getGenre(resultSet.getInt("film_id")))
                .build(), userId, friendId);
    }

    public Mpa getMpaById(int mpaId) {
        String sqlQuery =
                "SELECT rating_mpa_id, name " +
                        "FROM mpa_type " +
                        "WHERE rating_mpa_id=?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Рейтинг mpa не найден.");
        }
    }

    public Set<Genre> getGenre(int id) {
        Set<Genre> genreSet = new HashSet<>();

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT film_id, genre_id " +
                        "FROM genre " +
                        "ORDER BY genre_id ASC");

        while (genreRows.next()) {
            if (genreRows.getLong("film_id") == id) {
                genreSet.add(getGenreForId(genreRows.getInt("genre_id")));
            }
        }
        return genreSet;
    }

    public Genre getGenreForId(int id) {
        String sqlQuery =
                "SELECT genre_id, name " +
                        "FROM genre_type " +
                        "WHERE genre_id=?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Жанр не найден.");
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("rating_mpa_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

}
