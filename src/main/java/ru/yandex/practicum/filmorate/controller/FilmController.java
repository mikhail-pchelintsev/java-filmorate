package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film film) {
        return ResponseEntity.ok(service.create(film));
    }

    @PutMapping
    public ResponseEntity<Film> update(@RequestBody Film film) {
        return ResponseEntity.ok(service.update(film));
    }

    @GetMapping
    public Collection<Film> findAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable int id) {
        return service.getById(id);
    }
}
