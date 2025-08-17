package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage storage;
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User create(User user) {
        validate(user);
        normalize(user);
        return storage.create(user);
    }

    public User update(User user) {
        if (user.getId() == null || storage.findById(user.getId()).isEmpty())
            throw new NotFoundException("User not found");
        validate(user);
        normalize(user);
        return storage.update(user);
    }

    public Collection<User> getAll() {
        return storage.findAll();
    }

    public User getById(int id) {
        return storage.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void addFriend(int id, int friendId) {
        log.info("addFriend called with id = {} and friendId = {}", id, friendId);
        getById(id);
        getById(friendId);

        if (id == friendId) {
            log.warn("User {} cannot add themselves as a friend", id);
            throw new ValidationException("Cannot add self as friend");
        }

        log.info("Calling storage.addFriend for user {} and friend {}", id, friendId);
        storage.addFriend(id, friendId);
    }


    public int removeFriend(int userId, int friendId) {
        return storage.removeFriend(userId, friendId);
    }


    public List<User> getFriends(int id) {
        getById(id);
        return storage.getFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        getById(id);
        getById(otherId);
        return storage.getCommonFriends(id, otherId);
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@"))
            throw new ValidationException("Invalid email");
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" "))
            throw new ValidationException("Invalid login");
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("Birthday cannot be in the future");
    }

    private void normalize(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
