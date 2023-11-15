package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage inMemoryUserStorage;
    private int id = 0;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<User> getAll() {
        Collection<User> users = inMemoryUserStorage.getAllUsers();
        log.debug("Количество пользователей {}", users.size());
        return users;
    }

    public User getById(int id) {
        if (!inMemoryUserStorage.existById(id)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        return inMemoryUserStorage.getUserById(id);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(generateID());
        }
        inMemoryUserStorage.addUser(user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    public User update(User user) {
        if (!inMemoryUserStorage.existById(user.getId())) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        inMemoryUserStorage.updateUser(user);
        log.info("Данные пользователя изменены {}", user);
        return user;
    }

    public Collection<User> getListCommonFriends(int id, int otherId) {
        if (inMemoryUserStorage.getUserById(id).getFriends().isEmpty() || inMemoryUserStorage.getUserById(otherId).getFriends().isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> listFriendsId = inMemoryUserStorage.getUserById(id).getFriends();
        Set<Integer> otherFriendsId = inMemoryUserStorage.getUserById(otherId).getFriends();
        List<Integer> duplicates = new ArrayList<>();
        for (Integer i : listFriendsId) {
            if (otherFriendsId.contains(i)) {
                duplicates.add(i);
            }
        }
        return inMemoryUserStorage.getAllUsers().stream()
                .filter(user -> duplicates.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public Collection<User> getListFriends(int id) {
        if (inMemoryUserStorage.getUserById(id).getFriends().isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> listFriendsId = inMemoryUserStorage.getUserById(id).getFriends();
        return inMemoryUserStorage.getAllUsers().stream()
                .filter(user -> listFriendsId.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public User addFriend(int id, int friendId) {
        if (!inMemoryUserStorage.existById(id) || !inMemoryUserStorage.existById(friendId)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        User user = inMemoryUserStorage.getUserById(id);
        inMemoryUserStorage.getUserById(friendId).getFriends().add(id);
        user.getFriends().add(friendId);
        return user;
    }

    public User deleteFriend(int id, int friendId) {
        return inMemoryUserStorage.deleteFriend(id, friendId);
    }

    private int generateID() {
        return ++this.id;
    }
}