package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);
    User update(User user);
    Collection<User> findAll();
    Optional<User> findById(int id);
    void addFriend(int userId, int friendId);
    int removeFriend(int userId, int friendId);
    List<User> getFriends(int userId);
    List<User> getCommonFriends(int userId, int otherId);
}
