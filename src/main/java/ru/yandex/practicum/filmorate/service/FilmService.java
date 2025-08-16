package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage storage;

    public FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public Film create(Film film) {
        validate(film);
        return storage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() == null || storage.findById(film.getId()).isEmpty())
            throw new NotFoundException("Film not found");
        validate(film);
        return storage.update(film);
    }

    public Collection<Film> getAll() {
        return storage.findAll();
    }

    public Film getById(int id) {
        return storage.findById(id).orElseThrow(() -> new NotFoundException("Film not found"));
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
        // mpa и жанры подхватятся из БД через JOIN-ы в storage
    }
}
