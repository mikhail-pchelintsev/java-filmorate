package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User newUser) {
        validateUser(newUser);
        return userStorage.create(newUser);
    }

    public User update(User newUser) {
        validateUser(newUser);
        if (userStorage.getById(newUser.getId()) == null) {
            throw new NoSuchElementException("Не найден пользователей с id " + newUser.getId());
        }
        return userStorage.update(newUser);
    }

    public User getById(Long id) {
        return userStorage.getById(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    private void validateUser(User newUser) {
        if (newUser.getEmail() == null ||
                !newUser.getEmail().contains("@")) {
            throw new ValidationException("Email некорректный.");
        }
        if (newUser.getLogin() == null ||
                newUser.getLogin().isBlank()) {
            throw new NoSuchElementException("Логин пуст.");
        }
        if (newUser.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробел.");
        }
        if (newUser.getBirthday() == null ||
                newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения некорректная.");
        }
        if (newUser.getName() == null ||
                newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin()); // имя по логину
        }
    }
    
    public void addFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        if (user == null || friend == null) {
            throw new NoSuchElementException("Один из пользователей не найден.");
        }
        user.getFriendIds().add(friendId);
        friend.getFriendIds().add(userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        if (user == null || friend == null) {
            throw new NoSuchElementException("Один из пользователей не найден.");
        }
        user.getFriendIds().remove(friendId);
        friend.getFriendIds().remove(userId);
    }

    public Collection<User> getFriends(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с id " + userId + "не найден.");
        }
        return user.getFriendIds().stream()
                .map(this::getById)
                .filter(Objects::nonNull)
                .toList();
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = getById(userId);
        User other = getById(otherId);
        if (user == null || other == null) {
            throw new NoSuchElementException("Один из пользователей не найден.");
        }
        return user.getFriendIds().stream()
                .filter(other.getFriendIds()::contains)
                .map(this::getById)
                .filter(Objects::nonNull)
                .toList();
    }
}
