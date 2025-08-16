package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, kh);
        film.setId(Objects.requireNonNull(kh.getKey()).intValue());
        updateGenres(film);
        return findById(film.getId()).orElseThrow();
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id=?";
        int cnt = jdbc.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (cnt == 0) throw new NotFoundException("Film not found");
        updateGenres(film);
        return findById(film.getId()).orElseThrow();
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON m.id=f.mpa_id ORDER BY f.id";
        List<Film> films = jdbc.query(sql, this::mapFilm);
        loadGenres(films);
        return films;
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON m.id=f.mpa_id WHERE f.id=?";
        List<Film> films = jdbc.query(sql, this::mapFilm, id);
        if (films.isEmpty()) return Optional.empty();
        loadGenres(films);
        return Optional.of(films.get(0));
    }

    private Film mapFilm(ResultSet rs, int rn) throws SQLException {
        Film f = new Film();
        f.setId(rs.getInt("id"));
        f.setName(rs.getString("name"));
        f.setDescription(rs.getString("description"));
        f.setReleaseDate(rs.getDate("release_date").toLocalDate());
        f.setDuration(rs.getInt("duration"));
        f.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
        return f;
    }

    private void updateGenres(Film film) {
        jdbc.update("DELETE FROM film_genres WHERE film_id=?", film.getId());
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                jdbc.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), g.getId());
            }
        }
    }

    private void loadGenres(List<Film> films) {
        if (films.isEmpty()) return;
        String in = films.stream().map(f -> "?").collect(Collectors.joining(","));
        String sql = "SELECT fg.film_id, g.id, g.name " +
                "FROM film_genres fg JOIN genres g ON g.id=fg.genre_id " +
                "WHERE fg.film_id IN (" + in + ") ORDER BY g.id";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, films.stream().map(Film::getId).toArray());
        Map<Integer, List<Genre>> byFilm = new HashMap<>();
        for (Map<String, Object> r : rows) {
            int filmId = (Integer) r.get("film_id");
            int gid = (Integer) r.get("id");
            String gname = (String) r.get("name");
            byFilm.computeIfAbsent(filmId, k -> new ArrayList<>()).add(new Genre(gid, gname));
        }
        for (Film f : films) {
            f.setGenres(byFilm.getOrDefault(f.getId(), new ArrayList<>()));
        }
    }
}
