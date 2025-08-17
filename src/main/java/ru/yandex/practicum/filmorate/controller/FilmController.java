package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film film) {
        return ResponseEntity.ok(service.create(film));
    }

    @PutMapping
    public ResponseEntity<Film> update(@RequestBody Film film) {
        return ResponseEntity.ok(service.update(film));
    }

    @GetMapping
    public Collection<Film> getAll() {
        Collection<Film> films = service.getAll();

        if (films == null || films.isEmpty()) {
            throw new NotFoundException("Фильмов нет");
        }
        return films;
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable int id) {
        return service.getById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return service.getPopular(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable int id, @PathVariable int userId) {
        service.addLike(id, userId);
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable int id, @PathVariable int userId) {
        service.removeLike(id, userId);
        return ResponseEntity.status(204).build();
    }
}
