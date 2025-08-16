package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbc;

    public Collection<Mpa> findAll() {
        return jdbc.query("SELECT * FROM mpa ORDER BY id", this::map);
    }

    public Optional<Mpa> findById(int id) {
        List<Mpa> list = jdbc.query("SELECT * FROM mpa WHERE id=?", this::map, id);
        return list.stream().findFirst();
    }

    private Mpa map(ResultSet rs, int rn) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }
}
