package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(toMap(director)).intValue());
        return director;
    }

    @Override
    public Optional<Director> update(Director director) {
        String sqlQuery = "UPDATE DIRECTORS SET NAME = ? WHERE DIRECTOR_ID = ?";
        int result = jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        if (result == 0) {
            return Optional.empty();
        }
        return findById(director.getId());
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";

        int result = jdbcTemplate.update(sql, id);
        if (result == 1)
            log.info("Удалён режиссёер id {}", id);
        else
            throw new NotFoundException("Режиссёер для удаления не найден.");

    }

    @Override
    public Optional<Director> findById(Integer id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?", id);
        if (directorRows.next()) {
            return Optional.of(directorRows(directorRows));
        } else log.info("Режиссёер с идентификатором {} не найден.", id);
        return Optional.empty();
    }

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query("SELECT * FROM DIRECTORS", (rs, rowNum) -> makeDirector(rs));
    }


    private Map<String, Object> toMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        return values;
    }

    private Director directorRows(SqlRowSet rs) {
        return new Director(rs.getInt("DIRECTOR_ID"),
                rs.getString("NAME"));
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return new Director(rs.getInt("DIRECTOR_ID"),
                rs.getString("NAME"));
    }
}
