package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);


    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createPost(@RequestBody Film newFilm) {
        log.info("Получен запрос на создание фильма: {}", newFilm);
        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (newFilm.getDescription() == null || newFilm.getDescription().length() > 200) {
            log.error("Описание должно быть не больше 200 символов");
            throw new ValidationException("Описание поста не может быть пустым");
        }

        if (newFilm.getReleaseDate() == null ||
            newFilm.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.error("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (newFilm.getDuration() == null || newFilm.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм успешно создан: {}", newFilm);
        return newFilm;
    }

    public long getNextId() {
        Long maxId = films.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return maxId + 1;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма: {}", newFilm);
        if (newFilm.getName() == null) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (newFilm.getDescription().length() > 200 || newFilm.getDescription() == null) {
            throw new ValidationException("Описание должно быть не больше 200 символов");
        }

        if (newFilm.getReleaseDate() == null ||
            newFilm.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (newFilm.getDuration() == null || newFilm.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        if (!films.containsKey(newFilm.getId())) {
            throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм успешно обновлён: {}", newFilm);
        return newFilm;
    }
}
