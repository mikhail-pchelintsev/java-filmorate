package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Film getById(Long id);

    Collection<Film> getAll();

    public boolean exists(Long id);

    public boolean userHasLiked(Long filmId, Long userId);

    public Collection<Film> getTopPopular(int count);
}
