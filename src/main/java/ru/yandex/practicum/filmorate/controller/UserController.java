package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.ok(service.create(user));
    }

    @PutMapping
    public ResponseEntity<User> update(@RequestBody User user) {
        return ResponseEntity.ok(service.update(user));
    }

    @GetMapping
    public Collection<User> findAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return service.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable int id, @PathVariable int friendId) {
        try {
            service.addFriend(id, friendId);
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        int removed = service.removeFriend(id, friendId);

        if (removed == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        List<User> friends = service.getFriends(id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        List<User> commonFriends = service.getCommonFriends(id, otherId);
        return commonFriends;
    }

    @GetMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Boolean> isFriend(@PathVariable int id, @PathVariable int friendId) {
        List<User> friends = service.getFriends(id);
        boolean result = friends.stream().anyMatch(f -> f.getId() == friendId);
        return ResponseEntity.ok(result);
    }
}
