package ru.yandex.practicum.filmorate.control;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пользователей всего {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Нет имени при регистрации у пользователя: {}", user.getEmail());
        }
        if (user.getId() == 0) {
            user.setId(generateid());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user.getEmail());
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.debug("Меняется пользователь который не зарегистрирован: {}", user.getEmail());
            throw new InvalidEmailException("Такой логин не зарегистрирован!");
        } else {
            users.put(user.getId(), user);
            log.info("Успешно изменены данные пользователя: {}", user.getEmail());
        }
        return user;
    }

    private int generateid() {
        return ++id;
    }

}
