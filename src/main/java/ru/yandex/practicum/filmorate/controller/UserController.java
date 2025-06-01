package ru.yandex.practicum.filmorate.controller;

import org.slf4j.*;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createPost(@RequestBody User newUser) {
        log.info("Получен запрос на создание пользователя: {}", newUser);
        if (newUser.getEmail() == null || !newUser.getEmail().contains("@")) {
            log.error("Email не может быть пустым и должен содержать '@'");
            throw new ValidationException("Email не может быть пустым и должен содержать '@'");
        }

        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым");
        }

        if (newUser.getBirthday() == null || newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь успешно создан: {}", newUser);
        return newUser;
    }

    public long getNextId() {
        Long maxId = users.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return maxId + 1;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);

        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с id {} не найден", newUser.getId());
            throw new ValidationException("Пользователь с таким id не найден");
        }

        if (newUser.getEmail() == null || !newUser.getEmail().contains("@")) {
            log.error("Email не может быть пустым и должен содержать '@'");
            throw new ValidationException("Email не может быть пустым и должен содержать '@'");
        }

        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы");
        }

        if (newUser.getBirthday() == null || newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        users.put(newUser.getId(), newUser);
        log.info("Пользователь успешно обновлен: {}", newUser);
        return newUser;
    }
}

