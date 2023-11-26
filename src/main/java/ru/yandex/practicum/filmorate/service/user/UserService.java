package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
        return userStorage.getUserById(userId);
    }

    public User deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
        return userStorage.getUserById(userId);
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.getFriendsByUserId(userId);
    }

    public List<User> getMutualFriends(int userId, int otherId) {
        return userStorage.getMutualFriends(userId, otherId);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.findAllUsers();
    }

    public User getUserById(@PathVariable int id) {
        return userStorage.getUserById(id);
    }


}
