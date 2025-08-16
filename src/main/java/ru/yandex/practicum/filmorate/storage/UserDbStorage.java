package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;

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
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbc.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        int removed = jdbc.update("DELETE FROM friendships WHERE user_id=? AND friend_id=?", userId, friendId);
        if (removed == 0) {
            int removedReverse = jdbc.update("DELETE FROM friendships WHERE user_id=? AND friend_id=?", friendId, userId);
            if (removedReverse > 0) {
                jdbc.update("INSERT INTO friendships (user_id, friend_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                        userId, friendId);
            }

        }
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql =
                "SELECT u.* FROM friendships f " +
                        "JOIN users u ON u.id = f.friend_id " +
                        "WHERE f.user_id=? ORDER BY u.id";
        return jdbc.query(sql, this::map, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql =
                "SELECT u.* FROM users u " +
                        "JOIN friendships f1 ON u.id = f1.friend_id AND f1.user_id=? " +
                        "JOIN friendships f2 ON u.id = f2.friend_id AND f2.user_id=? " +
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
