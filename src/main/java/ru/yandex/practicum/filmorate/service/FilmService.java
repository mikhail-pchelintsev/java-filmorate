package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class FilmService {

    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film create(Film newFilm) {
        validateFilm(newFilm);
        return filmStorage.create(newFilm);
    }

    public Film update(Film newFilm) {
        validateFilm(newFilm);
        if (filmStorage.getById(newFilm.getId()) == null) {
            throw new NoSuchElementException("Фильм с id = " + newFilm.getId() + " не найден.");
        }
        return filmStorage.update(newFilm);
    }

    public Film getById(Long id) {
        return filmStorage.getById(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    private void validateFilm(Film newFilm) {
        if (newFilm.getName() == null ||
                newFilm.getName().isBlank()) {
            throw new ValidationException("название не может быть пустым.");
        }
        if (newFilm.getDescription() == null ||
                newFilm.getDescription().length() > 200) {
            throw new ValidationException("описание не может быть длиннее 200 знаков.");
        }
        if (newFilm.getReleaseDate() == null ||
                newFilm.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза некорректная.");
        }
        if (newFilm.getDuration() == null ||
                newFilm.getDuration() <= 0) {
            throw new ValidationException("Длительность фильма некорректная.");
        }
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getById(filmId);
        if (film == null) {
            throw new NoSuchElementException("Фильм с id " + filmId + "не найден.");
        }
        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь с id " + userId + " уже поставил лайк фильму с id " + filmId);
        }
        film.getLikes().add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = getById(filmId);
        if (film == null) {
            throw new NoSuchElementException("Фильм с id " + filmId + "не найден.");
        }
        if (!film.getLikes().contains(userId)) {
            throw new NoSuchElementException("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
        }
        film.getLikes().remove(userId);
    }

    public Collection<Film> getPopularMovies(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }
}
