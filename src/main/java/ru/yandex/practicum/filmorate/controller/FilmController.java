package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.Film;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAll(){
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable Long id){
        return filmService.getById(id);
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm){
        return filmService.create(newFilm);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm){
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId){
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId){
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count){
        return filmService.getPopularMovies(count);
    }
}


