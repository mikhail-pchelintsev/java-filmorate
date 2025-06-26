package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friendIds = new HashSet<>();

    public void addFriend(Long friendId) {
        if (friendId != null) {
            friendIds.add(friendId);
        }
    }

    public void removeFriend(Long friendId) {
        if (friendId != null) {
            friendIds.remove(friendId);
        }
    }
}
