package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public Film create(Film film) {
        film.setId(idCounter.incrementAndGet());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean exists(Long id) {
        return films.containsKey(id);
    }

    @Override
    public boolean userHasLiked(Long filmId, Long userId) {
        Film film = films.get(filmId);
        return film != null && film.getLikes().contains(userId);
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getTopPopular(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    @Override
    public Film getById(Long id) {
        return films.get(id);
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }
}
