package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
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
    private final DirectorStorage directorStorage;

    @Override
    public List<Film> findAllFilms() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", this::mapRowToFilm);
        films.forEach(film -> film.setDirectors(getFilmDirectors(film.getId())));
        return films;
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
        addDirectorForCurrentFilm(film);
        film.setDirectors(getFilmDirectors(film.getId()));
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
        updateDirectorsFilm(film);
        film.setGenres(genreDbStorage.getGenreForCurrentFilm(film.getId()));
        film.setDirectors(getFilmDirectors(film.getId()));

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
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            if (film != null) {
                film.setDirectors(getFilmDirectors(film.getId()));
            }
            return film;
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

    @Override
    public void deleteFilm(int filmId) {
        getFilmById(filmId);
        jdbcTemplate.update("DELETE FROM FILMS WHERE FILM_ID = ?", filmId);
    }

    @Override
    public LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy) {
        Optional<Director> director = directorStorage.findById(directorId);
        if (director.isEmpty()) {
            throw new NotFoundException("Режиссёер не найден");
        }
        SqlRowSet sql;
        if (sortBy.equals("year")) {
            sql = jdbcTemplate.queryForRowSet("SELECT f.* " +
                    "FROM DIRECTOR_FILMS AS df " +
                    "JOIN FILMS AS f ON df.FILM_ID = f.FILM_ID " +
                    "WHERE DIRECTOR_ID = ? " +
                    "GROUP BY f.FILM_ID, f.RELEASE_DATE " +
                    "ORDER BY f.RELEASE_DATE", directorId);

        } else if (sortBy.equals("likes")) {
            sql = jdbcTemplate.queryForRowSet("SELECT f.* " +
                    "FROM DIRECTOR_FILMS AS df " +
                    "JOIN FILMS AS f ON df.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN LIKES AS l On f.FILM_ID = l.FILM_ID " +
                    "WHERE DIRECTOR_ID = ? " +
                    "GROUP BY f.FILM_ID, l.FILM_ID IN (SELECT FILM_ID FROM LIKES) " +
                    "ORDER BY COUNT(l.FILM_ID) DESC", directorId);

        } else {
            log.error("Ошибка в sortBy");
            throw new ValidationException("Ошибка в sortBy");
        }
        Collection<Film> films = new ArrayList<>();
        while (sql.next()) {
            Film film = Film.builder()
                    .id(sql.getInt("film_id"))
                    .name(sql.getString("name"))
                    .description(sql.getString("description"))
                    .releaseDate(Objects.requireNonNull(sql.getDate("release_date")).toLocalDate())
                    .duration(sql.getInt("duration"))
                    .mpa(mpaDbStorage.getMpa(sql.getInt("rating_mpa_id")))
                    .build();
            film.setGenres(genreDbStorage.getGenreForCurrentFilm(film.getId()));
            film.setDirectors(getFilmDirectors(film.getId()));
            films.add(film);
        }
        return new LinkedHashSet<>(films);

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

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("director_id");
        String name = rs.getString("name");
        return Director.builder()
                .id(id)
                .name(name)
                .build();
    }

    public void addDirectorForCurrentFilm(Film film) {
        if (Objects.isNull(film.getDirectors())) {
            return;
        }
        try {
            film.getDirectors().forEach(d -> {
                String sqlQuery =
                        "INSERT INTO DIRECTOR_FILMS(FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
                jdbcTemplate.update(sqlQuery,
                        film.getId(),
                        d.getId());
            });
        } catch (DataIntegrityViolationException e) {
            log.error("Один из режисcёров не найден: {}", film.getDirectors());
            throw new NotFoundException("Один из режиcсёров не найден: " + film.getDirectors());
        }
    }

    public LinkedHashSet<Director> getFilmDirectors(Integer filmId) {
        String sql =
                "SELECT d.DIRECTOR_ID, d.name " +
                        "FROM DIRECTOR_FILMS AS df " +
                        "LEFT JOIN DIRECTORS AS d ON df.DIRECTOR_ID = d.DIRECTOR_ID " +
                        "WHERE df.film_id = ?";

        Collection<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), filmId);
        return new LinkedHashSet<>(directors);
    }

    public void updateDirectorsFilm(Film film) {
        String sql =
                "DELETE " +
                        "FROM DIRECTOR_FILMS " +
                        "WHERE FILM_ID = ?";

        jdbcTemplate.update(sql, film.getId());
        addDirectorForCurrentFilm(film);
    }
}
