package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmDbStorage filmStorage,
                       @Qualifier("userDbStorage") UserDbStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() == null || filmStorage.findById(film.getId()).isEmpty())
            throw new NotFoundException("Film not found");
        validate(film);
        return filmStorage.update(film);
    }

    public Collection<Film> getAll() {
        Collection<Film> films = filmStorage.findAll();
        if (films == null || films.isEmpty()) {
            throw new NotFoundException("Фильмов нет");
        }
        return films;
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    public Film getById(int id) {
        return filmStorage.findById(id).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank())
            throw new ValidationException("Film name must not be blank");
        if (film.getDescription() != null && film.getDescription().length() > 200)
            throw new ValidationException("Description too long");
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_BIRTHDAY))
            throw new ValidationException("Release date is too early");
        if (film.getDuration() == null || film.getDuration() <= 0)
            throw new ValidationException("Duration must be positive");
    }

    public void addLike(int filmId, int userId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            throw new NotFoundException("Film not found");
        }
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            throw new NotFoundException("Film not found");
        }
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        filmStorage.removeLike(filmId, userId);
    }
}
