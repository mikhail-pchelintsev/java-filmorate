package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbc;

    public Collection<Genre> findAll() {
        return jdbc.query("SELECT * FROM genre ORDER BY id", this::map);
    }

    public Optional<Genre> findById(int id) {
        List<Genre> list = jdbc.query("SELECT * FROM genre WHERE id=?", this::map, id);
        return list.stream().findFirst();
    }

    private Genre map(ResultSet rs, int rn) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
