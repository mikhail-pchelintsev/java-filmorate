package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, kh);
        user.setId(Objects.requireNonNull(kh.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";
        int cnt = jdbc.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        if (cnt == 0) throw new NotFoundException("User not found");
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return jdbc.query("SELECT * FROM users ORDER BY id", this::map);
    }

    @Override
    public Optional<User> findById(int id) {
        List<User> list = jdbc.query("SELECT * FROM users WHERE id=?", this::map, id);
        return list.stream().findFirst();
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (findById(userId).isEmpty() || findById(friendId).isEmpty()) {
            throw new NotFoundException("User not found");
        }

        if (userId == friendId) {
            throw new ValidationException("Cannot add self as friend");
        }

        String sql = "MERGE INTO friendship (user_id, friend_id, status) " +
                "KEY(user_id, friend_id) " +
                "VALUES (?, ?, 'ACCEPTED')";

        log.info("Before adding friend, user1's friends: {}", getFriends(userId));
        log.info("Before adding friend, user2's friends: {}", getFriends(friendId));

        jdbc.update(sql, userId, friendId);

        log.info("After adding friend, user1's friends: {}", getFriends(userId));
        log.info("After adding friend, user2's friends: {}", getFriends(friendId));
    }

    @Override
    public int removeFriend(int userId, int friendId) {
        Optional<User> user = findById(userId);
        Optional<User> friend = findById(friendId);

        if (user.isEmpty() || friend.isEmpty()) {
            throw new NotFoundException("User or friend not found");
        }

        String sql = "DELETE FROM friendship WHERE user_id=? AND friend_id=?";
        int removed = jdbc.update(sql, userId, friendId);

        if (removed == 0) {
            return 0;
        }
        return removed;
    }

    @Override
    public List<User> getFriends(int userId) {
        if (findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }

        String sql =
                "SELECT u.* FROM friendship f " +
                        "JOIN users u ON u.id = f.friend_id " +
                        "WHERE f.user_id=? AND f.status='ACCEPTED' " +
                        "ORDER BY u.id";

        return jdbc.query(sql, this::map, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql =
                "SELECT u.* FROM users u " +
                        "JOIN friendship f1 ON u.id = f1.friend_id AND f1.user_id=? AND f1.status='ACCEPTED' " +
                        "JOIN friendship f2 ON u.id = f2.friend_id AND f2.user_id=? AND f2.status='ACCEPTED' " +
                        "ORDER BY u.id";

        return jdbc.query(sql, this::map, userId, otherId);
    }

    private User map(ResultSet rs, int rn) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setName(rs.getString("name"));
        u.setBirthday(rs.getDate("birthday").toLocalDate());
        return u;
    }
}
