package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreDbStorage storage;

    public GenreController(GenreDbStorage storage) { this.storage = storage; }

    @GetMapping
    public Collection<Genre> findAll() {
        return storage.findAll();
    }

    @GetMapping("/{id}")
    public Genre get(@PathVariable int id) {
        return storage.findById(id).orElseThrow(() -> new NotFoundException("Genre not found"));
    }
}
