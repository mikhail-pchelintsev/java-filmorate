package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate() == null)
            throw new ValidationException("releaseDate cannot be null");

        if (film.getMpa() == null || film.getMpa().getId() == null)
            throw new ValidationException("mpa cannot be null");

        Integer mpaId = film.getMpa().getId();
        String sql = "SELECT COUNT(*) FROM mpa_rating WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, mpaId);

        if (count == null || count == 0) {
            throw new NotFoundException("MPA Rating not found with ID: " + mpaId);
        }

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("release_date", Date.valueOf(film.getReleaseDate()));
        params.put("duration", film.getDuration());
        params.put("mpa_rating_id", mpaId);

        Number key = insert.executeAndReturnKey(params);
        film.setId(key.intValue());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String genreSql = "SELECT COUNT(*) FROM genre WHERE id = ?";
                Integer genreCount = jdbc.queryForObject(genreSql, Integer.class, genre.getId());

                if (genreCount == null || genreCount == 0) {
                    throw new NotFoundException("Genre not found with ID: " + genre.getId());
                }
            }
        }
        updateGenres(film);
        return findById(film.getId()).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE film SET name=?, description=?, release_date=?, duration=?, mpa_rating_id=? WHERE id=?";
        int cnt = jdbc.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (cnt == 0) throw new NotFoundException("Film not found");
        updateGenres(film);
        return findById(film.getId()).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM film f JOIN mpa_rating m ON m.id=f.mpa_rating_id ORDER BY f.id";
        List<Film> films = jdbc.query(sql, this::mapFilm);
        loadGenres(films);

        if (films.isEmpty()) {
            throw new NotFoundException("Фильмов нет");
        }
        return films;
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM film f JOIN mpa_rating m ON m.id=f.mpa_rating_id WHERE f.id=?";
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
        Date release = rs.getDate("release_date");
        f.setReleaseDate(release != null ? release.toLocalDate() : null);
        f.setDuration(rs.getInt("duration"));
        f.setMpa(new Mpa(rs.getInt("mpa_rating_id"), rs.getString("mpa_name")));
        return f;
    }

    private void updateGenres(Film film) {
        // Удаляем старые жанры
        jdbc.update("DELETE FROM film_genre WHERE film_id=?", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            System.out.println("Updating genres for film " + film.getId() + ": " + film.getGenres());
            for (Genre g : film.getGenres()) {
                jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", film.getId(), g.getId());
            }
        }
    }


    private void loadGenres(List<Film> films) {
        if (films.isEmpty()) return;

        // Список ID фильмов
        List<Integer> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        System.out.println("Films to load genres: " + filmIds);

        // Формируем SQL с конкретными числами (без ?)
        String inClause = filmIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = "SELECT fg.film_id, g.id, g.name " +
                "FROM film_genre fg JOIN genre g ON g.id=fg.genre_id " +
                "WHERE fg.film_id IN (" + inClause + ") " +
                "ORDER BY g.id";

        System.out.println("SQL: " + sql);

        List<Map<String, Object>> rows = jdbc.queryForList(sql);

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

    @Override
    public List<Film> getPopular(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS like_count " +
                "FROM film f " +
                "JOIN mpa_rating m ON m.id = f.mpa_rating_id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id, m.name " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";

        List<Film> films = jdbc.query(sql, this::mapFilmWithLikes, count);
        loadGenres(films);
        return films;
    }

    // Новый метод маппинга для getPopular
    private Film mapFilmWithLikes(ResultSet rs, int rowNum) throws SQLException {
        Film f = new Film();
        f.setId(rs.getInt("id"));
        f.setName(rs.getString("name"));
        f.setDescription(rs.getString("description"));
        Date release = rs.getDate("release_date");
        f.setReleaseDate(release != null ? release.toLocalDate() : null);
        f.setDuration(rs.getInt("duration"));
        f.setMpa(new Mpa(rs.getInt("mpa_rating_id"), rs.getString("mpa_name")));

        // Устанавливаем количество лайков (новое поле в Film)
        f.setLikesCount(rs.getInt("like_count"));

        return f;
    }

    public void addLike(int filmId, int userId) {
        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        jdbc.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
    }


}
