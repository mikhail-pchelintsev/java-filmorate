package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaDbStorage storage;

    public MpaController(MpaDbStorage storage) { this.storage = storage; }

    @GetMapping
    public Collection<Mpa> findAll() {
        return storage.findAll();
    }

    @GetMapping("/{id}")
    public Mpa get(@PathVariable int id) {
        return storage.findById(id).orElseThrow(() -> new NotFoundException("Mpa not found"));
    }
}
