package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    void addUser(User user);

    void deleteUser(int id);

    void updateUser(User film);

    Collection<User> getAllUsers();

    User getUserById(int id);

    boolean existById(int id);

    User deleteFriend(int id, int idFriend);
}